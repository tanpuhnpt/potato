package com.ktpm.potatoapi.merchant.service;

import com.ktpm.potatoapi.user.entity.Role;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.cuisinetype.entity.CuisineType;
import com.ktpm.potatoapi.cuisinetype.repo.CuisineTypeRepository;
import com.ktpm.potatoapi.mail.MailService;
import com.ktpm.potatoapi.merchant.dto.MerchantRegistrationRequest;
import com.ktpm.potatoapi.merchant.dto.MerchantRegistrationResponse;
import com.ktpm.potatoapi.merchant.dto.MerchantResponse;
import com.ktpm.potatoapi.merchant.dto.MerchantUpdateRequest;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
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
    SecurityUtils securityUtils;

    @Override
    public List<MerchantRegistrationResponse> getAllRegisteredMerchants() {
        List<RegisteredMerchant> registeredMerchants = registeredMerchantRepository.findAll();
        log.info("Get all registered merchants");
        return registeredMerchants
                .stream()
                .map(registeredMerchant -> {
                    MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
                    response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
                    return response;
                })
                .toList();
    }

    @Override
    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists with email: {}", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTED);
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

        try {
            registeredMerchantRepository.save(registeredMerchant);
            log.info("{} registered merchant {}", request.getEmail(), registeredMerchant.getMerchantName());

            MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
            response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.MERCHANT_EXISTED);
        }
    }

    @Override
    @Transactional
    public MerchantRegistrationResponse approveMerchant(Long id) throws MessagingException {
        RegisteredMerchant registeredMerchant = registeredMerchantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTERED_MERCHANT_NOT_FOUND));

        if (registeredMerchant.getRegistrationStatus() != RegistrationStatus.PENDING)
            throw new AppException(ErrorCode.REGISTERED_MERCHANT_STATUS_NOT_PENDING);

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
        registeredMerchant.setRegistrationStatus(RegistrationStatus.APPROVED);

        // tạo merchant và gán merchant admin đã tạo
        Merchant merchant = Merchant.builder()
                .name(registeredMerchant.getMerchantName())
                .address(registeredMerchant.getAddress())
                .cuisineTypes(new HashSet<>(registeredMerchant.getCuisineTypes()))
                .merchantAdmin(merchantAdmin)
                .build();
        merchantRepository.save(merchant);

        // gửi mail phê duyệt
        mailService.sendApprovalEmail(registeredMerchant.getEmail(), merchant.getName(), rawPassword);

        log.info("Approve and Created merchant {}", merchant.getName());

        MerchantRegistrationResponse response = registeredMerchantMapper.toResponse(registeredMerchant);
        response.setCuisineTypes(mapCuisineTypeNames(registeredMerchant.getCuisineTypes()));
        return response;
    }

    @Override
    public List<MerchantResponse> getAllMerchantsForSysAdmin() {
        List<Merchant> merchants = merchantRepository.findAll();
        log.info("Get all merchants for System Admin");
        return merchants
                .stream()
                .map(merchant -> {
                    MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
                    merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
                    return merchantResponse;
                })
                .toList();
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
        Merchant merchant = securityUtils.getCurrentMerchant();

        log.info("Get {}'s information", merchant.getName());

        MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
        merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return merchantResponse;
    }

    @Override
    public MerchantResponse updateMyMerchant(MerchantUpdateRequest request, MultipartFile imgFile) {
        Merchant merchant = securityUtils.getCurrentMerchant();

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
        Merchant merchant = securityUtils.getCurrentMerchant();

        merchant.setOpen(isOpen);
        merchantRepository.save(merchant);

        log.info("Updated {}'s open status to {}", merchant.getName(), isOpen);

        MerchantResponse response = merchantMapper.toResponse(merchant);
        response.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
        return response;
    }

    @Override
    public List<MerchantResponse> getAllMerchantsForCustomer() {
        List<Merchant> merchants = merchantRepository.findAllByIsOpenTrue();
        log.info("Get all merchants for Customer");
        return merchants
                .stream()
                .map(merchant -> {
                    MerchantResponse merchantResponse = merchantMapper.toResponse(merchant);
                    merchantResponse.setCuisineTypes(mapCuisineTypeNames(merchant.getCuisineTypes()));
                    return merchantResponse;
                })
                .toList();
    }

    @Override
    public MerchantResponse getMerchantForCustomer(Long id) {
        Merchant merchant = merchantRepository.findById(id)
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
}