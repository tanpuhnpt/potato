package com.ktpm.potatoapi.cuisinetype.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CuisineType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String name;

    String imgUrl;
    boolean isVisible;

    @PrePersist
    protected void onCreate() {
        this.isVisible = true;
    }
}
