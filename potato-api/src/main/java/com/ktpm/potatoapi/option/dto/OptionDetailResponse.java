package com.ktpm.potatoapi.option.dto;

import com.ktpm.potatoapi.menu.dto.MenuItemInOptionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionDetailResponse {
    Long id;
    String name;
    boolean isRequired;
    boolean isVisible;
    boolean isActive;
    List<OptionValueResponse> optionValues;
    List<MenuItemInOptionResponse> menuItems;
}
