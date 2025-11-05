package com.ktpm.potatoapi.merchant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionUploadRequest {
    String merchantName;

    @NotNull
    MultipartFile imgFile;
}
