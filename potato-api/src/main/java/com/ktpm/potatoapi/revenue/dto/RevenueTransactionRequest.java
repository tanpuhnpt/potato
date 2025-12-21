package com.ktpm.potatoapi.revenue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueTransactionRequest {
    @NotBlank(message = "TRANSACTION_DESCRIPTION_BLANK")
    String description;

    @NotNull(message = "TRANSACTION_IMG_NULL")
    MultipartFile imgFile;
}
