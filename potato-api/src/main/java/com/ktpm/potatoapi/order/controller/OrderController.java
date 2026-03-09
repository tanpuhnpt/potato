package com.ktpm.potatoapi.order.controller;

import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.dto.OrderStatusUpdateRequest;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import com.ktpm.potatoapi.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "APIs for order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class OrderController {
    OrderService orderService;

    @PostMapping("/check-out")
    @Operation(summary = "Create a new order",
            description = "API for Customer to create a order")
    public ResponseEntity<?> checkout(@RequestBody @Valid OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Show orders in progress",
            description = "API for Customer to retrieve a list of orders which are confirmed/delivering")
    public ResponseEntity<?> getAllOrdersInProgress() {
        return ResponseEntity.ok(orderService.getAllOrdersInProgress());
    }

    @GetMapping("/my-order-history")
    @Operation(summary = "Show order history for Customer",
            description = "API for Customer to retrieve a list of orders which are completed/canceled")
    public ResponseEntity<?> getOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getOrderHistory(page, size));
    }

    @GetMapping("/merchant/my-orders")
    @Operation(summary = "Show orders of Merchant by status",
            description = "API for Merchant Admin to retrieve a list of orders by status")
    public ResponseEntity<?> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrdersOfMyMerchantByStatus(status, page, size));
    }

    @GetMapping(path = {"/orders/{orderId}", "merchant/orders/{orderId}"})
    @Operation(summary = "Show detail of order",
            description = "API for both Customer and Merchant Admin to show detail of an order")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @PatchMapping("/merchant/orders/{orderId}")
    @Operation(summary = "Update status of an order",
            description = "API for Merchant Admin to update status of an order")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                                 @RequestBody @Valid OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }

    @PatchMapping("/orders/{orderId}/confirm")
    @Operation(summary = "Confirm an order",
            description = "API for Customer to confirm an order which is completed")
    public ResponseEntity<?> confirmOrderCompleted(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.confirmOrderCompleted(orderId));
    }
}
