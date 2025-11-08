package com.ktpm.potatoapi.order.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer(User customer);
    List<Order> findAllByMerchant(Merchant merchant);
    boolean existsByIdAndCustomerId(Long id, Long customerId);
    boolean existsByIdAndMerchantId(Long id, Long merchantId);
}
