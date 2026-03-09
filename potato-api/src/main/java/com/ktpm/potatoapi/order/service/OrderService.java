package com.ktpm.potatoapi.order.service;

import com.ktpm.potatoapi.common.pagination.PageResponse;
import com.ktpm.potatoapi.order.dto.OrderResponse;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {
    // services for customer
    OrderResponse createOrder(OrderRequest cartOrderRequest);
    List<OrderResponse> getAllOrdersInProgress();
    PageResponse<OrderResponse> getOrderHistory(int page, int size);
    OrderResponse confirmOrderCompleted(Long orderId);

    // mutual service
    OrderResponse getOrderDetail(Long orderId);

    // services for merchant admin
    List<OrderResponse> getAllOrdersOfMyMerchant();
    OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request);
}
