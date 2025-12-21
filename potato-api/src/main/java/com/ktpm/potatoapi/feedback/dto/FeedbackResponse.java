package com.ktpm.potatoapi.feedback.dto;

import com.ktpm.potatoapi.user.dto.UserInFeedbackResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long id;
    String comment;
    LocalDateTime createdAt;
    UserInFeedbackResponse user;
    List<String> imgUrl;
}
