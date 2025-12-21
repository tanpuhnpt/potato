package com.ktpm.potatoapi.order.repo;

import com.ktpm.potatoapi.order.entity.OrderItem;
import com.ktpm.potatoapi.order.entity.OrderItemOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemOptionValueRepository extends JpaRepository<OrderItemOptionValue, Long> {
    List<OrderItemOptionValue> findAllByOrderItem(OrderItem orderItem);
}
