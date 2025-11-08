package com.ktpm.potatoapi.order.service;

import com.ktpm.potatoapi.order.dto.OrderResponse;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {
    // services for customer
    OrderResponse createOrder(OrderRequest cartOrderRequest);
    List<OrderResponse> getAllOrdersInProgress();
    List<OrderResponse> getOrderHistory();

    // mutual service
    OrderResponse getOrderDetail(Long orderId);

    // services for merchant admin
    List<OrderResponse> getAllOrdersOfMyMerchant();
    OrderResponse updateStatusOrder(Long orderId, OrderStatusUpdateRequest request);
}
