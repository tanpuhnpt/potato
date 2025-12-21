package com.ktpm.potatoapi.payment.controller;

import com.ktpm.potatoapi.payment.dto.PaymentRequest;
import com.ktpm.potatoapi.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Payment Controller", description = "APIs for payment")
@CrossOrigin("*")
public class PaymentController {
    PaymentService paymentService;

    @GetMapping("create-payment-url")
    public ResponseEntity<?> pay(HttpServletRequest httpServletRequest, PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(httpServletRequest, paymentRequest));
    }
}
