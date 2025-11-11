package com.ktpm.potatoapi.category.controller;

import com.ktpm.potatoapi.category.dto.CategoryRequest;
import com.ktpm.potatoapi.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category APIs", description = "APIs for category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class CategoryController {
    CategoryService categoryService;

    @GetMapping("/merchant/categories")
    @Operation(summary = "Show all categories for Merchant Admin",
            description = "API for Merchant Admin to retrieve a list of all categories")
    public ResponseEntity<?> getAllCategoriesForMerAdmin() {
        return ResponseEntity.ok(categoryService.getAllCategoriesOfMyMerchant());
    }

    @PostMapping("/merchant/categories")
    @Operation(summary = "Create a new category",
            description = "API for Merchant Admin to create a new category")
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
    }

    @PutMapping("/merchant/categories/{id}")
    @Operation(summary = "Update category", description = "API for Merchant Admin to update category")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/merchant/categories/{id}")
    @Operation(summary = "Delete category", description = "API for Merchant Admin to delete category")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/merchants/{merchantId}/categories")
    @Operation(summary = "Show all categories of a merchant for Customer",
            description = "API for Customer to retrieve a list of all categories of a merchant")
    public ResponseEntity<?> getAllCategoriesForCustomer(@PathVariable Long merchantId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesForCustomer(merchantId));
    }
}
