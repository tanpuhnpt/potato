package com.ktpm.potatoapi.cart.controller;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cart Controller", description = "APIs for cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class CartController {
    CartService cartService;

    @PostMapping("/cart")
    @Operation(summary = "Check menu item in cart",
            description = "API to validate menu items in a cart are from the same merchant")
    public ResponseEntity<?> checkMenuItemsInCart(@RequestBody @Valid List<CartItemRequest> cartRequest,
                                                 @RequestParam Long menuItemId) {
        return  ResponseEntity.ok(cartService.isSameMerchant(cartRequest, menuItemId));
    }
}
