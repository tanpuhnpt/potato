package com.ktpm.potatoapi.merchant.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByName(String merchantName);
    Optional<Merchant> findByMerchantAdmin_Email(String email);
    List<Merchant> findAllByIsOpenTrue();
    Optional<Merchant> findByIdAndIsActiveTrue(Long id);
}
