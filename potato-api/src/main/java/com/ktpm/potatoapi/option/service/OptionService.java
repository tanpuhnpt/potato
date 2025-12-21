package com.ktpm.potatoapi.option.service;

import com.ktpm.potatoapi.option.dto.*;

import java.util.List;

public interface OptionService {
    // services for Merchant Admin
    List<OptionResponse> getAllOptionsOfMyMerchant();
    OptionDetailResponse getOptionForMerAdmin(Long optionId);
    OptionResponse createOptionAndOptionValue(OptionCreationRequest request);
    OptionResponse createOptionValueForExistingOption(Long optionId, OptionValueRequest request);
    OptionResponse updateOption(Long optionId, OptionUpdateRequest request);
    OptionValueResponse updateOptionValue(Long id, OptionValueRequest request);
    OptionValueResponse updateOptionValueVisibleStatus(Long valueId, boolean isVisible);
    OptionDetailResponse assignMenuItemToOption(Long optionId, AddMenuItemToOptionRequest request);
    void deleteOptionValue(Long valueId);
    void deleteOption(Long optionId);
}
