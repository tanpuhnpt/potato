package com.ktpm.potatoapi.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInFeedbackResponse {
    Long id;
    String email;
    String fullName;
    String role;
}
