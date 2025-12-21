package com.ktpm.potatoapi.menu.dto;

import com.ktpm.potatoapi.category.dto.CategoryResponse;
import com.ktpm.potatoapi.option.dto.OptionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDetailResponse {
    Long id;
    CategoryResponse category;
    String name;
    String description;
    Long basePrice;
    String imgUrl;
    List<OptionResponse> options;
}
