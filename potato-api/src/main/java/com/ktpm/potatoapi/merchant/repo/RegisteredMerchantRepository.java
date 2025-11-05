package com.ktpm.potatoapi.merchant.repo;

import com.ktpm.potatoapi.merchant.entity.RegisteredMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredMerchantRepository extends JpaRepository<RegisteredMerchant, Long> {
    boolean existsByMerchantName(String merchantName);

    Optional<RegisteredMerchant> findByMerchantName(String merchantName);
}
