package com.ktpm.potatoapi.option.entity;

import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`option`", uniqueConstraints = {@UniqueConstraint(columnNames = {"merchant_id", "active_name"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    boolean isRequired;
    boolean isVisible;
    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (IF(is_active, name, NULL)) STORED")
    @Generated
    String activeName;

    @ManyToOne
    Merchant merchant;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OptionValue> optionValues;

    @ManyToMany
    @JoinTable(
            name = "option_menu_items",
            joinColumns = @JoinColumn(name = "option_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    List<MenuItem> menuItems;


    @PrePersist
    protected void onCreate() {
        this.isActive = true;
        this.isVisible = true;
        this.menuItems = new ArrayList<>();
    }
}
