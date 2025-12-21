package com.ktpm.potatoapi.category.repo;

import com.ktpm.potatoapi.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByMerchantIdAndIsActiveTrue(Long merchantId);
    Optional<Category> findByIdAndIsActiveTrue(Long id);
}
