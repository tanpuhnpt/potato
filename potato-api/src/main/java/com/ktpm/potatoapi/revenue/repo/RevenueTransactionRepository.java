package com.ktpm.potatoapi.revenue.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.revenue.dto.RevenueResponse;
import com.ktpm.potatoapi.revenue.entity.RevenueTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueTransactionRepository extends JpaRepository<RevenueTransaction, Long> {
    @Query("""
            SELECT new com.ktpm.potatoapi.revenue.dto.RevenueResponse(
                SUM(o.totalAmount), SUM(o.totalAmount - o.deliveryFee)
            )
            FROM Order o
            WHERE o.merchant.id = :merchantId
            AND o.status = 'COMPLETED'
            AND o.createdAt BETWEEN :startDate AND :endDate
        """)
    RevenueResponse getNetRevenueByMerchantAndDateRange(@Param("merchantId") Long merchantId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    List<RevenueTransaction> getRevenueTransactionsByCreatedAtBetweenAndMerchant(
            LocalDateTime start, LocalDateTime end, Merchant merchant);
}
