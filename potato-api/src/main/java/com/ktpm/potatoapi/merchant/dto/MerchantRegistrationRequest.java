package com.ktpm.potatoapi.merchant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantRegistrationRequest {
    @NotBlank(message = "REGISTERED_MERCHANT_ADMIN_FULL_NAME_BLANK")
    String fullName;

    @NotBlank(message = "EMAIL_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "REGISTERED_MERCHANT_NAME_BLANK")
    String merchantName;

    @NotBlank(message = "ADDRESS_BLANK")
    String address;

    @NotNull(message = "LATITUDE_NULL")
    double latitude;

    @NotNull(message = "LONGITUDE_NULL")
    double longitude;

    @NotEmpty(message = "CUISINE_TYPES_EMPTY")
    Set<String> cuisineTypes;
}