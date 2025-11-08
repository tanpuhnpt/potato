package com.ktpm.potatoapi.feedback.service;

import com.ktpm.potatoapi.feedback.dto.FeedbackResponse;
import com.ktpm.potatoapi.feedback.dto.GiveFeedbackRequest;
import com.ktpm.potatoapi.feedback.dto.ReplyFeedbackRequest;

public interface FeedbackService {
    void giveFeedback(GiveFeedbackRequest request);
    FeedbackResponse replyFeedback(ReplyFeedbackRequest request);
}
