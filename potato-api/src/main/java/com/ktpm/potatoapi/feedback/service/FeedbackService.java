package com.ktpm.potatoapi.feedback.service;

import com.ktpm.potatoapi.feedback.dto.GiveFeedbackRequest;

public interface FeedbackService {
    void giveFeedback(GiveFeedbackRequest request);
}
