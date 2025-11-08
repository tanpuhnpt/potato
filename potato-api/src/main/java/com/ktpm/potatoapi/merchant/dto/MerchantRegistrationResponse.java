package com.ktpm.potatoapi.merchant.dto;

import com.ktpm.potatoapi.merchant.entity.RegistrationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantRegistrationResponse {
    Long id;
    String fullName;
    String email;
    String merchantName;
    String address;
    Set<String> cuisineTypes;
    RegistrationStatus registrationStatus;
    String imgUrl;
}