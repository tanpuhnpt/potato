package com.ktpm.potatoapi.cart.service;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;

import java.util.List;

public interface CartService {
    boolean isSameMerchant(List<CartItemRequest> cartRequest, Long menuItemId);
}
