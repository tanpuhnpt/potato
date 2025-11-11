package com.ktpm.potatoapi.option.controller;

import com.ktpm.potatoapi.option.dto.AddMenuItemToOptionRequest;
import com.ktpm.potatoapi.option.dto.OptionCreationRequest;
import com.ktpm.potatoapi.option.dto.OptionUpdateRequest;
import com.ktpm.potatoapi.option.dto.OptionValueRequest;
import com.ktpm.potatoapi.option.service.OptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Option APIs", description = "APIs for option")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class OptionController {
    OptionService optionService;

    @GetMapping("/merchant/options")
    @Operation(summary = "Show all options in my merchant",
            description = "API for Merchant Admin to retrieve a list of all options")
    public ResponseEntity<?> getAllOptionsForMerAdmin() {
        return ResponseEntity.ok(optionService.getAllOptionsOfMyMerchant());
    }

    @GetMapping("/merchant/options/{optionId}")
    @Operation(summary = "Show an option for Merchant Admin",
            description = "API for Merchant Admin to retrieve an option with associated menu items")
    public ResponseEntity<?> getOptionForMerAdmin(@PathVariable Long optionId) {
        return ResponseEntity.ok(optionService.getOptionForMerAdmin(optionId));
    }

    @PostMapping("/merchant/options")
    @Operation(summary = "Create a new option and option values",
            description = "API for Merchant Admin to create a new option and its option value at once")
    public ResponseEntity<?> createOptionAndOptionValue(@RequestBody @Valid OptionCreationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(optionService.createOptionAndOptionValue(request));
    }

    @PostMapping("/merchant/options/{optionId}/values")
    @Operation(summary = "Create a new option value",
            description = "API for Merchant Admin to create a new option value for an existing option")
    public ResponseEntity<?> createOptionValueForExistingOption(@PathVariable Long optionId,
                                               @RequestBody @Valid OptionValueRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(optionService.createOptionValueForExistingOption(optionId, request));
    }

    @PutMapping("merchant/options/{optionId}")
    @Operation(summary = "Update option information",
            description = "API for Merchant Admin to update an option information")
    public ResponseEntity<?> updateOption(@PathVariable Long optionId, OptionUpdateRequest request) {
        return ResponseEntity.ok(optionService.updateOption(optionId, request));
    }

    @PutMapping("merchant/options/values/{valueId}")
    @Operation(summary = "Update option value information",
            description = "API for Merchant Admin to update an option value information")
    public ResponseEntity<?> updateOptionValue(@PathVariable Long valueId, OptionValueRequest request) {
        return ResponseEntity.ok(optionService.updateOptionValue(valueId, request));
    }

    @PatchMapping("/merchant/options/values/{valueId}/isVisible")
    @Operation(summary = "Update an option value's visible status",
            description = "API for Merchant Admin to update an option value's visible status")
    public ResponseEntity<?> updateOptionValueVisibleStatus(@PathVariable Long valueId,
                                                            @RequestParam boolean isVisible) {
        return ResponseEntity.ok(optionService.updateOptionValueVisibleStatus(valueId, isVisible));
    }

    @PostMapping("/merchant/options/{optionId}/assign-menu-items")
    @Operation(summary = "Assign menu items to an existing option",
            description = "API for Merchant Admin to assign menu items to an existing option")
    public ResponseEntity<?> assignMenuItemToOption(@PathVariable Long optionId,
                                                    @RequestBody AddMenuItemToOptionRequest request) {
        return ResponseEntity.ok(optionService.assignMenuItemToOption(optionId, request));
    }

    @DeleteMapping("/merchant/options/values/{valueId}")
    @Operation(summary = "Delete option value",
            description = "API for Merchant Admin to delete option value")
    public ResponseEntity<?> deleteOptionValue(@PathVariable Long valueId) {
        optionService.deleteOptionValue(valueId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/merchant/options/{optionId}")
    @Operation(summary = "Delete option",
            description = "API for Merchant Admin to delete option and its values")
    public ResponseEntity<?> deleteOption(@PathVariable Long optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.ok().build();
    }
}
