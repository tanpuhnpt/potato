package com.ktpm.potatoapi.order.repo;

import com.ktpm.potatoapi.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
