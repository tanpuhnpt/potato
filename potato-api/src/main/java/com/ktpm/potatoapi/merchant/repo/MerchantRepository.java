package com.ktpm.potatoapi.merchant.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByName(String merchantName);
    Optional<Merchant> findByMerchantAdmin_Email(String email);
    Optional<Merchant> findByIdAndIsActiveTrue(Long id);
    
    @Query("""
        SELECT m
        FROM Merchant m
        WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:active IS NULL OR m.isActive = :active)
        AND (:open IS NULL OR m.isOpen = :open)
    """)
    Page<Merchant> findAllMerchants(
            @Param("name") String name,
            @Param("active") Boolean active,
            @Param("open") Boolean open,
            Pageable pageable
    );
}
