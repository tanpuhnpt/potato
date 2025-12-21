package com.ktpm.potatoapi.revenue.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueTransactionResponse {
    Long id;
    String description;
    String imgUrl;
    LocalDateTime createdAt;
}
