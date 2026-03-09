package com.ktpm.potatoapi.merchant.repo;

import com.ktpm.potatoapi.merchant.entity.RegisteredMerchant;
import com.ktpm.potatoapi.merchant.entity.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RegisteredMerchantRepository extends JpaRepository<RegisteredMerchant, Long> {
    boolean existsByMerchantName(String merchantName);
    Optional<RegisteredMerchant> findByMerchantName(String merchantName);

    @Query("""
        SELECT rm
        FROM RegisteredMerchant rm
        WHERE (:status IS NULL OR rm.registrationStatus = :status)
    """)
    Page<RegisteredMerchant> findByRegistrationStatus(RegistrationStatus status, Pageable pageable);
}
