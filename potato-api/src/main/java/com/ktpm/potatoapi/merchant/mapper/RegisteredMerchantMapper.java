package com.ktpm.potatoapi.merchant.mapper;

import com.ktpm.potatoapi.merchant.dto.MerchantRegistrationRequest;
import com.ktpm.potatoapi.merchant.dto.MerchantRegistrationResponse;
import com.ktpm.potatoapi.merchant.entity.RegisteredMerchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisteredMerchantMapper {
    @Mapping(target = "cuisineTypes", ignore = true)
    RegisteredMerchant toEntity(MerchantRegistrationRequest dto);

    @Mapping(target = "cuisineTypes", ignore = true)
    MerchantRegistrationResponse toResponse(RegisteredMerchant entity);
}