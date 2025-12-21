package com.ktpm.potatoapi.merchant.mapper;

import com.ktpm.potatoapi.merchant.dto.MerchantResponse;
import com.ktpm.potatoapi.merchant.dto.MerchantUpdateRequest;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.entity.RegisteredMerchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    @Mapping(target = "cuisineTypes", ignore = true)
    MerchantResponse toResponse(Merchant entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "merchantName")
    Merchant toMerchant(RegisteredMerchant registeredMerchant);

    @Mapping(target = "cuisineTypes", ignore = true)
    @Mapping(target = "imgUrl", ignore = true)
    void update(@MappingTarget Merchant entity, MerchantUpdateRequest dto);
}