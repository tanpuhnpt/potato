package com.ktpm.potatoapi.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantUpdateRequest {
    @NotBlank(message = "INTRO_BLANK")
    String introduction;

    @NotBlank(message = "ADDRESS_BLANK")
    String address;

    @NotNull(message = "LATITUDE_NULL")
    double latitude;

    @NotNull(message = "LONGITUDE_NULL")
    double longitude;

    @NotEmpty(message = "OPENING_HOURS_EMPTY")
    Map<String, String> openingHours;

    @NotEmpty(message = "CUISINE_TYPES_EMPTY")
    Set<String> cuisineTypes;
}