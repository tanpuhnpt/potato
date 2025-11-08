package com.ktpm.potatoapi.feedback.controller;

import com.ktpm.potatoapi.feedback.dto.GiveFeedbackRequest;
import com.ktpm.potatoapi.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Feedback Controller", description = "APIs for rating")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @PostMapping(value = "/give-feedback", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Give a feedback on an order",
            description = "API for Customer to give a feedback on an order")
    public ResponseEntity<?> giveFeedBack(@ModelAttribute @Valid GiveFeedbackRequest request) {
        feedbackService.giveFeedback(request);
        return ResponseEntity.ok().build();
    }
}
