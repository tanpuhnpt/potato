package com.ktpm.potatoapi.merchant.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantResponse {
    Long id;
    String name;
    String introduction;
    String address;
    double latitude;
    double longitude;
    Map<String, String> openingHours;
    BigDecimal avgRating;
    int ratingCount;
    boolean isActive;
    boolean isOpen;
    String imgUrl;
    Set<String> cuisineTypes;
}