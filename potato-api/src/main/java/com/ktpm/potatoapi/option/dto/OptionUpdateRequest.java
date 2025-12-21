package com.ktpm.potatoapi.option.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionUpdateRequest {
    @NotBlank(message = "OPTION_NAME_BLANK")
    String name;
}
