package com.ktpm.potatoapi.option.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionValueResponse {
    Long id;
    String name;
    Long extraPrice;
    boolean isDefault;
    boolean isVisible;
    boolean isActive;
}
