package com.ktpm.potatoapi.order.dto;

import com.ktpm.potatoapi.feedback.dto.FeedbackResponse;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    String code;
    String fullName;
    String phone;
    String note;
    String deliveryAddress;
    Long deliveryFee;
    Long totalAmount;
    String cancelReason;
    OrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<OrderItemResponse> orderItems;
    List<FeedbackResponse> feedbacks;
}
