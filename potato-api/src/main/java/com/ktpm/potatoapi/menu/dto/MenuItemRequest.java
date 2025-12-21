package com.ktpm.potatoapi.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemRequest {
    @NotBlank(message = "MENU_ITEM_NAME_BLANK")
    String name;

    String description;

    @NotNull(message = "MENU_ITEM_PRICE_NULL")
    @Positive(message = "MENU_ITEM_PRICE_NEG_OR_ZERO")
    Long basePrice;

    @NotNull(message = "MENU_ITEM_FILE_NULL")
    MultipartFile imgFile;

    @NotNull(message = "MENU_ITEM_CATEGORY_NULL")
    Long categoryId;
}
