package com.ktpm.potatoapi.menu.entity;

import com.ktpm.potatoapi.category.entity.Category;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.option.entity.Option;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"merchant_id", "active_name"}))
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String description;
    Long basePrice;
    boolean isVisible;
    boolean isActive;

    String imgUrl;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (IF(is_active, name, NULL)) STORED")
    @Generated
    String activeName;

    @ManyToOne
    Merchant merchant;

    @ManyToOne
    Category category;

    @ManyToMany(mappedBy = "menuItems")
    List<Option> options;

    @PrePersist
    protected void onCreate() {
        this.isVisible = true;
        this.isActive = true;
    }
}
