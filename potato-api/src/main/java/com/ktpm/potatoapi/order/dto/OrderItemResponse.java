package com.ktpm.potatoapi.order.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long id;
    String menuItemName;
    Long menuItemBasePrice;
    Integer quantity;
    Long subtotal;
    String note;
    List<OrderItemOptionValueResponse> optionValues;
}
