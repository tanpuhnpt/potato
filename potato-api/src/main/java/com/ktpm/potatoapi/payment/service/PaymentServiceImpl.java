package com.ktpm.potatoapi.payment.service;

import com.ktpm.potatoapi.common.config.payment.VNPayConfig;
import com.ktpm.potatoapi.common.utils.VNPayUtils;
import com.ktpm.potatoapi.payment.dto.PaymentRequest;
import com.ktpm.potatoapi.payment.dto.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
    VNPayConfig vnPayConfig;

    @Override
    public PaymentResponse createPayment(HttpServletRequest httpServletRequest, PaymentRequest paymentRequest) {
        long amount = paymentRequest.getAmount() * 100L;

        Map<String, String> params = vnPayConfig.getVNPayConfig();
        params.put("vnp_Amount", String.valueOf(amount));

        // build query url
        List fieldNames = new ArrayList(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        queryUrl += "&vnp_SecureHash=" + VNPayUtils.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + queryUrl;

        return new PaymentResponse(paymentUrl);
    }
}
