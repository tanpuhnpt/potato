package com.ktpm.potatoapi.order.entity;

import com.ktpm.potatoapi.menu.entity.MenuItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Order order;

    @ManyToOne
    MenuItem menuItem;

    String menuItemName;
    Long menuItemBasePrice;
    Integer quantity;
    Long subtotal;
    String note;
}
