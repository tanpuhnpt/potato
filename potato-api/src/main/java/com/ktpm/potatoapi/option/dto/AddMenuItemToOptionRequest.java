package com.ktpm.potatoapi.option.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddMenuItemToOptionRequest {
    List<Long> menuItemIds;
}
