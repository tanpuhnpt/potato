package com.ktpm.potatoapi.option.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionCreationRequest {
    @NotBlank(message = "OPTION_NAME_BLANK")
    String name;

    @NotNull(message = "OPTION_REQUIRED_STATUS_NULL")
    boolean isRequired;

    @NotEmpty(message = "OPTION_VALUES_EMPTY")
    List<OptionValueRequest> optionValues;
}
