package com.ktpm.potatoapi.merchant.service;

import com.ktpm.potatoapi.common.pagination.PageResponse;
import com.ktpm.potatoapi.merchant.dto.*;
import com.ktpm.potatoapi.redis.RedisService;
import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.cuisinetype.entity.CuisineType;
import com.ktpm.potatoapi.cuisinetype.repo.CuisineTypeRepository;
import com.ktpm.potatoapi.mail.MailService;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.entity.RegisteredMerchant;
import com.ktpm.potatoapi.merchant.entity.RegistrationStatus;
import com.ktpm.potatoapi.merchant.mapper.MerchantMapper;
import com.ktpm.potatoapi.merchant.mapper.RegisteredMerchantMapper;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.merchant.repo.RegisteredMerchantRepository;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MerchantServiceImpl implements MerchantService {
    MerchantRepository merchantRepository;
    RegisteredMerchantRepository registeredMerchantRepository;
    RegisteredMerchantMapper registeredMerchantMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    CuisineTypeRepository cuisineTypeRepository;
    MailService mailService;
    MerchantMapper merchantMapper;
    CloudinaryService cloudinaryService;
    MerchantContextProvider merchantContextProvider;
    RedisService redisService;

    @Override
    public PageResponse<MerchantRegistrationResponse> getRegisteredMerchantsByStatus(RegistrationStatus status, int page, int size) {
        String key = String.format("registeredmerchant:%s:%d:%d", status.name(), page, size);
        PageResponse<MerchantRegistrationResponse> pageResponse = redisService.get(key, PageResponse.class);

        if (pageResponse == null) {
            log.info("query registered merchant");
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            pageResponse = PageResponse.from(registeredMerchantRepository
                    .findByRegistrationStatus(status, pageable)
                    .map(registeredMerchant -> {
                        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
                        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
                        return response;
                    }));

            redisService.save(key, pageResponse);
        }

        return pageResponse;
    }

    @Override
    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists with email: {}", request.getEmail());
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_ADMIN_EXISTED);
        }
        if (registeredMerchantRepository.existsByMerchantName(request.getMerchantName())) {
            log.error("This merchant name ({}) is already registered", request.getMerchantName());
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_EXISTED);
        }
        if (merchantRepository.existsByName(request.getMerchantName())){
            log.error("Merchant already exists with name: {}", request.getMerchantName());
            throw new AppException(ErrorCode.MERCHANT_EXISTED);
        }

        RegisteredMerchant registeredMerchant = registeredMerchantMapper.toEntity(request);
        registeredMerchant.setCuisineTypes(mapCuisineTypes(request.getCuisineTypes()));

        registeredMerchantRepository.save(registeredMerchant);
        log.info("{} registered merchant {}", request.getEmail(), registeredMerchant.getMerchantName());

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    @Transactional
    public MerchantRegistrationResponse approveRegistration(Long id) throws MessagingException {
        RegisteredMerchant registeredMerchant = registeredMerchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTERED_MERCHANT_NOT_FOUND));

        if (registeredMerchant.getRegistrationStatus() != RegistrationStatus.PENDING)
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_STATUS_NOT_PENDING);

        mailService.sendRegistrationApprovalEmail(registeredMerchant.getEmail(),
                registeredMerchant.getFullName(), registeredMerchant.getMerchantName());

        registeredMerchant.setRegistrationStatus(RegistrationStatus.APPROVED);
        registeredMerchantRepository.save(registeredMerchant);

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    public MerchantRegistrationResponse rejectRegistration(Long id) throws MessagingException {
        RegisteredMerchant registeredMerchant = registeredMerchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTERED_MERCHANT_NOT_FOUND));

        if (registeredMerchant.getRegistrationStatus() != RegistrationStatus.PENDING)
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_STATUS_NOT_PENDING);

        mailService.sendRegistrationRejectionEmail(registeredMerchant.getEmail(), registeredMerchant.getFullName());

        registeredMerchant.setRegistrationStatus(RegistrationStatus.REJECTED);
        registeredMerchantRepository.save(registeredMerchant);

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    public MerchantRegistrationResponse uploadTransactionImg(TransactionUploadRequest request) {
        RegisteredMerchant registeredMerchant = registeredMerchantRepository.findByMerchantName(request.getMerchantName())
                .orElseThrow(() -> new AppException(ErrorCode.REGISTERED_MERCHANT_NOT_FOUND));

        registeredMerchant.setImgUrl(uploadTransactionImage(request.getImgFile()));
        registeredMerchant.setRegistrationStatus(RegistrationStatus.PAID);
        registeredMerchantRepository.save(registeredMerchant);

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    @Transactional
    public MerchantRegistrationResponse activateMerchant(Long id) throws MessagingException {
        RegisteredMerchant registeredMerchant = registeredMerchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTERED_MERCHANT_NOT_FOUND));

        if (registeredMerchant.getRegistrationStatus() != RegistrationStatus.PAID)
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_STATUS_NOT_PAID);

        // tạo account cho merchant admin
        String rawPassword = "12345678"; // default password
        User merchantAdmin = User.builder()
                .email(registeredMerchant.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .fullName(registeredMerchant.getFullName())
                .role(Role.MERCHANT_ADMIN)
                .build();
        userRepository.save(merchantAdmin);
        log.info("Created merchant admin with mail: {}", registeredMerchant.getEmail());

        // cập nhật trạng thái đăng kí kinh doanh
        registeredMerchant.setRegistrationStatus(RegistrationStatus.COMPLETED);

        // tạo merchant và gán merchant admin đã tạo
        Merchant merchant = merchantMapper.toMerchant(registeredMerchant);
        merchant.setMerchantAdmin(merchantAdmin);
        merchantRepository.save(merchant);

        // gửi mail kích hoạt
        mailService.sendMerchantActivationEmail(registeredMerchant.getEmail(), merchant.getName(), rawPassword);

        log.info("Approve and Created merchant {}", merchant.getName());

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    public PageResponse<MerchantResponse> getAllMerchantsForSysAdmin(
            String name, Boolean isActive, Boolean isOpen, int page, int size) {
        String key = String.format("merchant:%s:%b:%b:%d:%d", name, isActive, isOpen, page, size);
        PageResponse<MerchantResponse> pageResponse = redisService.get(key, PageResponse.class);

        if (pageResponse == null) {
            log.info("query merchant");
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            pageResponse = PageResponse.from(merchantRepository
                    .findAllMerchants(name, isActive, isOpen, pageable)
                    .map(merchant -> {
                        MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
                        merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
                        return merchantResponse;
                    }));

            redisService.save(key, pageResponse);
        }
        return pageResponse;
    }

    @Override
    public MerchantResponse getMerchantForSysAdmin(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        log.info("Get merchant {} for System Admin", merchant.getName());

        MerchantResponse response = merchantMapper.toResponse(merchant);
        response.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return response;
    }

    @Override
    public MerchantResponse updateMerchantActiveStatus(Long id, boolean isActive) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        merchant.setActive(isActive);
        merchant.setOpen(isActive);

        merchantRepository.save(merchant);

        log.info("Updated {}'s active status to {}", merchant.getName(), isActive);

        MerchantResponse response = merchantMapper.toResponse(merchant);
        response.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return response;
    }

    @Override
    public MerchantResponse getMyMerchant() {
        Merchant merchant = merchantContextProvider.getCurrentMerchant();

        log.info("Get {}'s information", merchant.getName());

        MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
        merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return merchantResponse;
    }

    @Override
    public MerchantResponse updateMyMerchant(MerchantUpdateRequest request, MultipartFile imgFile) {
        Merchant merchant = merchantContextProvider.getCurrentMerchant();

        merchantMapper.update(merchant, request);
        merchant.setCuisineTypes(mapCuisineTypes(request.getCuisineTypes()));
        merchant.setImgUrl(uploadMerchantImage(imgFile));
        merchantRepository.save(merchant);

        log.info("Updated {}'s information", merchant.getName());

        MerchantResponse response = merchantMapper.toResponse(merchant);
        response.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return response;
    }

    @Override
    public MerchantResponse updateMyMerchantOpenStatus(boolean isOpen) {
        Merchant merchant = merchantContextProvider.getCurrentMerchant();

        merchant.setOpen(isOpen);
        merchantRepository.save(merchant);

        log.info("Updated {}'s open status to {}", merchant.getName(), isOpen);

        MerchantResponse response = merchantMapper.toResponse(merchant);
        response.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return response;
    }

    @Override
    public PageResponse<MerchantResponse> getAllMerchantsForCustomer(String name, int page, int size) {
        String key = String.format("merchant:%s:%d:%d", name, page, size);
        PageResponse<MerchantResponse> pageResponse = redisService.get(key, PageResponse.class);

        if (pageResponse == null) {
            log.info("query merchant");
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

            pageResponse = PageResponse.from(merchantRepository
                    .findAllMerchants(name, true, true, pageable)
                    .map(merchant -> {
                        MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
                        merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
                        return merchantResponse;
                    }));

            redisService.save(key, pageResponse);
        }

        return pageResponse;
    }

    @Override
    public MerchantResponse getMerchantForCustomer(Long id) {
        Merchant merchant = merchantRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        if (!merchant.isOpen())
            throw new AppException(ErrorCode.MERCHANT_CLOSED);

        log.info("Get merchant {} for Customer", merchant.getName());

        MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
        merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return merchantResponse;
    }

    private Set<CuisineType> mapCuisineTypes(Set<String> cuisineTypeNames) {
        if (cuisineTypeNames == null) return new HashSet<>();
        return cuisineTypeNames.stream()
                .map(name -> cuisineTypeRepository.findByName(name)
                        .orElseThrow(() -> new AppException(ErrorCode.CUISINE_TYPE_NOT_FOUND)))
                .collect(Collectors.toSet());
    }

    private Set<String> mapCuisineTypeNames(Set<CuisineType> cuisineTypes) {
        if (cuisineTypes == null) return new HashSet<>();
        return cuisineTypes.stream()
                .map(CuisineType::getName)
                .collect(Collectors.toSet());
    }

    private String uploadMerchantImage(MultipartFile file) {
        return cloudinaryService.upload(file, "merchants");
    }

    private String uploadTransactionImage(MultipartFile file) {
        return cloudinaryService.upload(file, "registration transaction");
    }
}