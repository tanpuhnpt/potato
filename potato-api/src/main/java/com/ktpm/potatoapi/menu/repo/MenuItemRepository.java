package com.ktpm.potatoapi.menu.repo;

import com.ktpm.potatoapi.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByMerchantIdAndIsActiveTrue(Long merchantId);
    List<MenuItem> findAllByMerchantIdAndIsVisibleTrue(Long merchantId);
    Optional<MenuItem> findByIdAndIsActiveTrue(Long menuItemId);
    @Modifying
    @Query("UPDATE MenuItem m SET m.isActive = false, m.isVisible = false WHERE m.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    List<MenuItem> findAllByIdInAndMerchantIdAndIsActiveTrue(List<Long> menuItemIds, Long merchantId);
    Optional<MenuItem> findByIdAndIsVisibleTrue(Long menuItemId);
}
