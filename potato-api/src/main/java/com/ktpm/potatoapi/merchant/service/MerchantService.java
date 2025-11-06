package com.ktpm.potatoapi.merchant.service;

import com.ktpm.potatoapi.merchant.dto.*;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MerchantService {
    // services for merchant on-boarding
    List<MerchantRegistrationResponse> getAllRegisteredMerchants();
    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);
    MerchantRegistrationResponse confirmRegistration(Long id) throws MessagingException;
    MerchantRegistrationResponse uploadTransactionImg(TransactionUploadRequest request);
    MerchantRegistrationResponse approveMerchant(Long id) throws MessagingException;

    // services for SYSTEM ADMIN
    List<MerchantResponse> getAllMerchantsForSysAdmin();
    MerchantResponse getMerchantForSysAdmin(Long id);
    MerchantResponse updateMerchantActiveStatus(Long id, boolean isActive);

    // services for MERCHANT ADMIN
    MerchantResponse getMyMerchant();
    MerchantResponse updateMyMerchant(MerchantUpdateRequest request, MultipartFile imgFile);
    MerchantResponse updateMyMerchantOpenStatus(boolean isOpen);

    // services for CUSTOMER
    List<MerchantResponse> getAllMerchantsForCustomer();
    MerchantResponse getMerchantForCustomer(Long id);
}