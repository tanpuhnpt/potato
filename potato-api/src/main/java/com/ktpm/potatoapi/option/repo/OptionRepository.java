package com.ktpm.potatoapi.option.repo;

import com.ktpm.potatoapi.option.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByMerchantIdAndIsActiveTrue(Long merchantId);
    Optional<Option> findByIdAndIsActiveTrue(Long optionId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM option_menu_items WHERE menu_item_id = :menuItemId", nativeQuery = true)
    void deleteAllOptionLinksByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM option_menu_items
            WHERE menu_item_id IN (
                SELECT id FROM menu_item WHERE category_id = :categoryId
            )
            """,
            nativeQuery = true)
    void deleteAllOptionLinksByCategoryId(@Param("categoryId") Long categoryId);
}
