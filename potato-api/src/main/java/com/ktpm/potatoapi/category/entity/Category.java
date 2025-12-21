package com.ktpm.potatoapi.category.entity;

import com.ktpm.potatoapi.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"merchant_id", "active_name"}))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (IF(is_active, name, NULL)) STORED")
    @Generated
    String activeName;

    @ManyToOne
    Merchant merchant;

    @PrePersist
    protected void onCreate(){
        this.isActive = true;
    }
}
