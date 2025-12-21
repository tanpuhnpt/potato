package com.ktpm.potatoapi.option.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"option_id", "active_name"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    Long extraPrice;
    boolean isDefault;
    boolean isVisible;
    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (IF(is_active, name, NULL)) STORED")
    @Generated
    String activeName;

    @ManyToOne
    Option option;

    @PrePersist
    protected void onCreate() {
        this.isActive = true;
        this.isVisible = true;
    }
}
