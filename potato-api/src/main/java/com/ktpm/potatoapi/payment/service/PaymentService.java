package com.ktpm.potatoapi.payment.service;

import com.ktpm.potatoapi.payment.dto.PaymentRequest;
import com.ktpm.potatoapi.payment.dto.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponse createPayment(HttpServletRequest httpServletRequest, PaymentRequest paymentRequest);
}
