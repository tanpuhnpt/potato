package com.ktpm.potatoapi.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {
    @NotNull(message = "CART_MENU_ITEM_NULL")
    Long menuItemId;

    @NotNull(message = "CART_MENU_ITEM_QUANTITY_NULL")
    @Positive(message = "CART_MENU_ITEM_QUANTITY_NEG_OR_ZERO")
    Integer quantity;

    String note;
    List<Long> optionValueIds;
}
