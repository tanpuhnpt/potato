package com.ktpm.potatoapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogInRequest {
    @NotBlank(message = "EMAIL_BLANK")
    String email;

    @NotBlank(message = "PASSWORD_BLANK")
    String password;
}