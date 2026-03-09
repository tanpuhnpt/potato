package com.ktpm.potatoapi.merchant.service;

import com.ktpm.potatoapi.common.pagination.PageResponse;
import com.ktpm.potatoapi.merchant.dto.*;
import com.ktpm.potatoapi.merchant.entity.RegistrationStatus;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

public interface MerchantService {
    // services for merchant on-boarding
    PageResponse<MerchantRegistrationResponse> getRegisteredMerchantsByStatus(int page, int size, RegistrationStatus status);
    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);
    MerchantRegistrationResponse approveRegistration(Long id) throws MessagingException;
    MerchantRegistrationResponse rejectRegistration(Long id) throws MessagingException;
    MerchantRegistrationResponse uploadTransactionImg(TransactionUploadRequest request);
    MerchantRegistrationResponse activateMerchant(Long id) throws MessagingException;

    // services for SYSTEM ADMIN
    PageResponse<MerchantResponse> getAllMerchantsForSysAdmin(String name, Boolean isActive, Boolean isOpen, int page, int size);
    MerchantResponse getMerchantForSysAdmin(Long id);
    MerchantResponse updateMerchantActiveStatus(Long id, boolean isActive);

    // services for MERCHANT ADMIN
    MerchantResponse getMyMerchant();
    MerchantResponse updateMyMerchant(MerchantUpdateRequest request, MultipartFile imgFile);
    MerchantResponse updateMyMerchantOpenStatus(boolean isOpen);

    // services for CUSTOMER
    PageResponse<MerchantResponse> getAllMerchantsForCustomer(String name, int page, int size);
    MerchantResponse getMerchantForCustomer(Long id);
}