package com.ktpm.potatoapi.order.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMerchant(Merchant merchant);
    boolean existsByIdAndCustomerId(Long id, Long customerId);
    boolean existsByIdAndMerchantId(Long id, Long merchantId);

    @Query(value = """
        FROM Order o
        WHERE o.customer.id = :customerId
        AND o.status = 'CONFIRMED' or o.status = 'DELIVERING'
    """)
    List<Order> getOrderInProgressByCustomer(@Param("customerId") Long customerId);

    @Query(value = """
        FROM Order o
        WHERE o.customer.id = :customerId
        AND o.status = 'COMPLETED' or o.status = 'CANCELED'
    """)
    List<Order> getOrderHistoryByCustomer(@Param("customerId") Long customerId);

    Order findByDroneIdAndStatus(Long droneId, OrderStatus ready);
}
