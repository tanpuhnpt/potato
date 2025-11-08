package com.ktpm.potatoapi.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiveFeedbackRequest {
    @NotNull(message = "RATING_ORDER_NULL")
    Long orderId;

    @NotNull(message = "RATING_NULL")
    @Range(min = 1, max = 5, message = "RATING_OUT_OF_RANGE")
    Integer rating;

    String comment;
    List<MultipartFile> imgFiles;
}
