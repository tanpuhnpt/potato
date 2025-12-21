package com.ktpm.potatoapi.cuisinetype.service;

import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeRequest;
import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeResponse;

import java.util.List;

public interface CuisineTypeService {
    List<CuisineTypeResponse> getAllCuisineTypes();
    List<CuisineTypeResponse> getAllVisibleCuisineTypes();
    CuisineTypeResponse createCuisineType(CuisineTypeRequest request);
    CuisineTypeResponse updateCuisineTypeStatus(Long id, boolean isVisible);
}
