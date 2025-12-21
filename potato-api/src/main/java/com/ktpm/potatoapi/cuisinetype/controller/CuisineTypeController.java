package com.ktpm.potatoapi.cuisinetype.controller;

import com.ktpm.potatoapi.cuisinetype.dto.CuisineTypeRequest;
import com.ktpm.potatoapi.cuisinetype.service.CuisineTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cuisine Type APIs", description = "APIs for cuisine type")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class CuisineTypeController {
    CuisineTypeService cuisineTypeService;

    @GetMapping("/admin/cuisine-types")
    @Operation(summary = "Show all cuisine types in system",
            description = "API for System Admin to retrieve a list of all cuisine types")
    public ResponseEntity<?> getAllCuisineTypes() {
        return ResponseEntity.ok(cuisineTypeService.getAllCuisineTypes());
    }

    @GetMapping(path = {"/merchant/cuisine-types", "/cuisine-types"})
    @Operation(summary = "Show all visible cuisine types in system",
            description = "API for Merchant Admin or Customer to retrieve a list of all active cuisine types")
    public ResponseEntity<?> getAllVisibleCuisineTypes() {
        return ResponseEntity.ok(cuisineTypeService.getAllVisibleCuisineTypes());
    }

    @PostMapping(path = "/admin/cuisine-types", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new cuisine type",
            description = "API for System Admin to create a new cuisine type")
    public ResponseEntity<?> createCuisineType(@ModelAttribute @Valid CuisineTypeRequest request) {
        return ResponseEntity.ok(cuisineTypeService.createCuisineType(request));
    }

    @PatchMapping("/admin/cuisine-types/{id}/isVisible")
    @Operation(summary = "Update a cuisine type's visible status",
            description = "API for System Admin to delete a cuisine type")
    public ResponseEntity<?> updateCuisineTypeStatus(@PathVariable Long id, @RequestParam boolean isVisible) {
        return ResponseEntity.ok(cuisineTypeService.updateCuisineTypeStatus(id, isVisible));
    }
}
