package com.ktpm.potatoapi.feedback.service;

import com.ktpm.potatoapi.feedback.dto.FeedbackResponse;
import com.ktpm.potatoapi.feedback.dto.GiveFeedbackRequest;
import com.ktpm.potatoapi.feedback.dto.ReplyFeedbackRequest;

import java.util.List;

public interface FeedbackService {
    // services for Customer
    void giveFeedback(GiveFeedbackRequest request);
    List<FeedbackResponse> getAllFeedbacksForCustomer(Long merchantId);

    // services for Merchant Admin
    FeedbackResponse replyFeedback(Long id, ReplyFeedbackRequest request);
    List<FeedbackResponse> getAllFeedbacksOfMyMerchant();
}
