package com.ktpm.potatoapi.feedback.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyFeedbackRequest {
    Long orderId;
    String comment;
}
