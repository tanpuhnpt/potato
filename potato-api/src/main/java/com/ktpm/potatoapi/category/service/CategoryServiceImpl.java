package com.ktpm.potatoapi.category.service;

import com.ktpm.potatoapi.category.dto.CategoryRequest;
import com.ktpm.potatoapi.category.dto.CategoryResponse;
import com.ktpm.potatoapi.category.entity.Category;
import com.ktpm.potatoapi.category.mapper.CategoryMapper;
import com.ktpm.potatoapi.category.repo.CategoryRepository;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.option.repo.OptionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    SecurityUtils securityUtils;
    MerchantRepository merchantRepository;
    MenuItemRepository menuItemRepository;
    OptionRepository optionRepository;

    @Override
    public List<CategoryResponse> getAllCategoriesOfMyMerchant() {
        List<Category> categories = categoryRepository
                .findAllByMerchantIdAndIsActiveTrue(securityUtils.getCurrentMerchant().getId());

        log.info("Get all categories for Merchant Admin");

        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> getAllCategoriesForCustomer(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        if (!merchant.isOpen())
            throw new AppException(ErrorCode.MERCHANT_CLOSED);

        List<Category> categories = categoryRepository.findAllByMerchantIdAndIsActiveTrue(merchantId);

        log.info("Get all categories for Customer");

        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Merchant merchant = securityUtils.getCurrentMerchant();

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setMerchant(merchant);

        try {
            categoryRepository.save(category);
            log.info("{} created category {}", merchant.getName(), category.getName());
            return categoryMapper.toResponse(category);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Merchant merchant = securityUtils.getCurrentMerchant();

        // Check category must be owned of current merchant
        if (!category.getMerchant().equals(merchant))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        category.setName(categoryRequest.getName());

        try {
            categoryRepository.save(category);
            log.info("{} updated category: {}", merchant.getName(), category.getName());

            return categoryMapper.toResponse(category);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Merchant merchant = securityUtils.getCurrentMerchant();

        // Check category must be owned by current merchant
        if (!category.getMerchant().equals(merchant))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        // xóa các liên kết của option và menu item thuộc cate này
        optionRepository.deleteAllOptionLinksByCategoryId(id);

        // xóa các menu item thuộc category này
        menuItemRepository.deleteByCategoryId(category.getId());

        category.setActive(false);
        categoryRepository.save(category);

        log.info("{} deleted {} and its menu items", merchant.getName(), category.getName());
    }
}
