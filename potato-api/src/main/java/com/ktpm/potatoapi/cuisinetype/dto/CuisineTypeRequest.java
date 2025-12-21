package com.ktpm.potatoapi.cuisinetype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CuisineTypeRequest {
    @NotBlank(message = "CUISINE_TYPE_NAME_BLANK")
    String name;

    @NotNull(message = "CUISINE_TYPE_IMG_NULL")
    MultipartFile imgFile;
}
