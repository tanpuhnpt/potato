package com.ktpm.potatoapi.cuisinetype.service;

import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeRequest;
import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeResponse;
import com.ktpm.potatoapi.cuisinetype.entity.CuisineType;
import com.ktpm.potatoapi.cuisinetype.mapper.CuisineTypeMapper;
import com.ktpm.potatoapi.cuisinetype.repo.CuisineTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CuisineTypeServiceImpl implements CuisineTypeService {
    CuisineTypeRepository cuisineTypeRepository;
    CuisineTypeMapper mapper;
    CloudinaryService cloudinaryService;

    @Override
    public List<CuisineTypeResponse> getAllCuisineTypes() {
        log.info("Get all cuisine types successfully");
        return cuisineTypeRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CuisineTypeResponse> getAllVisibleCuisineTypes() {
        log.info("Get all visible cuisine types successfully");
        return cuisineTypeRepository.findAllByIsVisibleTrue().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CuisineTypeResponse createCuisineType(CuisineTypeRequest request) {
        CuisineType cuisineType = new CuisineType();
        cuisineType.setName(request.getName());
        cuisineType.setImgUrl(uploadCuisineTypeImage(request.getImgFile()));

        try {
            cuisineTypeRepository.save(cuisineType);
            log.info("Created cuisine type {}", request.getName());

            return mapper.toResponse(cuisineType);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CUISINE_TYPE_EXISTED);
        }
    }

    @Override
    public CuisineTypeResponse updateCuisineTypeStatus(Long id, boolean isVisible) {
        CuisineType cuisineType = cuisineTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUISINE_TYPE_NOT_FOUND));

        cuisineType.setVisible(isVisible);
        cuisineTypeRepository.save(cuisineType);
        log.info("Updated {}'s visible status", cuisineType.getName());

        return mapper.toResponse(cuisineType);
    }

    private String uploadCuisineTypeImage(MultipartFile file) {
        return cloudinaryService.upload(file, "cuisine_types");
    }
}
