package com.ktpm.potatoapi.merchant.controller;

import com.ktpm.potatoapi.merchant.dto.MerchantRegistrationRequest;
import com.ktpm.potatoapi.merchant.dto.MerchantUpdateRequest;
import com.ktpm.potatoapi.merchant.dto.TransactionUploadRequest;
import com.ktpm.potatoapi.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Merchant APIs", description = "APIs for merchant")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MerchantController {
    MerchantService merchantService;

    @GetMapping("/admin/registered-merchants")
    @Operation(summary = "Show all registered merchants in system",
            description = "API for System Admin to retrieve a list of all registered merchants")
    public ResponseEntity<?> getAllRegisteredMerchants() {
        return ResponseEntity.ok(merchantService.getAllRegisteredMerchants());
    }

    @PostMapping("/merchant/register")
    @Operation(summary = "Register a business",
            description = "API for Merchant Admin to register a business")
    public ResponseEntity<?> registerMerchant(@RequestBody @Valid MerchantRegistrationRequest request) {
        return ResponseEntity.ok(merchantService.registerMerchant(request));
    }

    @PostMapping("/admin/registered-merchants/{id}/approve")
    @Operation(summary = "Approve a registered merchant",
            description = "API for System Admin to approve a registered merchant")
    public ResponseEntity<?> approveMerchant(@PathVariable Long id) throws MessagingException {
        return ResponseEntity.ok(merchantService.approveMerchant(id));
    }

    @PostMapping("/admin/registered-merchants/{id}/confirm")
    @Operation(summary = "Confirm a registered merchant",
            description = "API for System Admin to confirm a registered merchant")
    public ResponseEntity<?> confirmRegistration(@PathVariable Long id) throws MessagingException {
        return ResponseEntity.ok(merchantService.confirmRegistration(id));
    }

    @PostMapping(path = "/merchant/upload-transaction-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload transaction image",
            description = "API for Merchant Admin to upload transaction image")
    public ResponseEntity<?> uploadTransactionImg(@ModelAttribute @Valid TransactionUploadRequest request) {
        return ResponseEntity.ok(merchantService.uploadTransactionImg(request));
    }

    @GetMapping("/admin/merchants")
    @Operation(summary = "Show all merchants for System Admin",
            description = "API for System Admin to retrieve a list of all merchants")
    public ResponseEntity<?> getAllMerchantsForSysAdmin() {
        return ResponseEntity.ok(merchantService.getAllMerchantsForSysAdmin());
    }

    @GetMapping("/admin/merchants/{id}")
    @Operation(summary = "Show a merchant for System Admin",
            description = "API for System Admin to retrieve a specific merchant")
    public ResponseEntity<?> getMerchantForSysAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getMerchantForSysAdmin(id));
    }

    @PatchMapping("/admin/merchants/{id}/isActive")
    @Operation(summary = "Change a merchant's active status",
            description = "API for System Admin to activate/deactivate a merchant")
    public ResponseEntity<?> updateMerchantActiveStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        return ResponseEntity.ok(merchantService.updateMerchantActiveStatus(id, isActive));
    }

    @GetMapping("/merchant/my-merchant")
    @Operation(summary = "Show my merchant information",
            description = "API for Merchant Admin to retrieve their own merchant information")
    public ResponseEntity<?> getMyMerchant() {
        return ResponseEntity.ok(merchantService.getMyMerchant());
    }

    @PutMapping(value = "/merchant/my-merchant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update my merchant information",
            description = "API for Merchant Admin to update their own merchant information")
    public ResponseEntity<?> updateMyMerchant(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("data")
            @Valid MerchantUpdateRequest request,

            @RequestParam("img") MultipartFile imgFile
    ) {
        return ResponseEntity.ok(merchantService.updateMyMerchant(request, imgFile));
    }

    @PatchMapping("/merchant/my-merchant/isOpen")
    @Operation(summary = "Update my merchant's open status",
            description = "API for Merchant Admin to update their own merchant's open status")
    public ResponseEntity<?> updateMyMerchantOpenStatus(@RequestParam boolean isOpen) {
        return ResponseEntity.ok(merchantService.updateMyMerchantOpenStatus(isOpen));
    }

    @GetMapping("/merchants")
    @Operation(summary = "Show all merchants for Customer",
            description = "API for Customer to retrieve a list of all merchants which are active and open")
    public ResponseEntity<?> getAllMerchantsForCustomer() {
        return ResponseEntity.ok(merchantService.getAllMerchantsForCustomer());
    }

    @GetMapping("/merchants/{id}")
    @Operation(summary = "Show a merchant for Customer",
            description = "API for Customer to retrieve a specific merchant which is active and open")
    public ResponseEntity<?> getMerchantForCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(merchantService.getMerchantForCustomer(id));
    }
}