package com.ktpm.potatoapi.order.entity;

import com.ktpm.potatoapi.option.entity.OptionValue;
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
public class OrderItemOptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    OrderItem orderItem;

    @ManyToOne
    OptionValue optionValue;

    String optionValueName;
    Long extraPrice;
}
