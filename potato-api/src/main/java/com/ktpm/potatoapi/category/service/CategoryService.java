package com.ktpm.potatoapi.category.service;

import com.ktpm.potatoapi.category.dto.CategoryRequest;
import com.ktpm.potatoapi.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    // services for Merchant Admin
    List<CategoryResponse> getAllCategoriesOfMyMerchant();
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);
    void deleteCategory(Long id);

    // services for Customer
    List<CategoryResponse> getAllCategoriesForCustomer(Long merchantId);
}
