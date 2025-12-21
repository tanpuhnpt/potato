package com.ktpm.potatoapi.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusUpdateRequest {
    @NotBlank(message = "ORDER_STATUS_BLANK")
    String status;

    String cancelReason;
}
