package com.ktpm.potatoapi.feedback.repo;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.merchant = :merchant AND f.rating IS NOT NULL")
    BigDecimal calcAvgRatingByMerchant(@Param("merchant") Merchant merchant);
}
