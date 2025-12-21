package com.ktpm.potatoapi.option.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionValueRequest {
    @NotBlank(message = "OPTION_VALUE_NAME_BLANK")
    String name;

    @PositiveOrZero(message = "OPTION_VALUE_EXTRA_PRICE_NEGATIVE")
    Long extraPrice;
}
