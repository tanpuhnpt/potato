package com.ktpm.potatoapi.option.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionResponse {
    Long id;
    String name;
    boolean isRequired;
    boolean isVisible;
    boolean isActive;
    List<OptionValueResponse> optionValues;
}
