package com.ktpm.potatoapi.merchant.entity;

import com.ktpm.potatoapi.cuisinetype.entity.CuisineType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisteredMerchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;
    String fullName;

    @Column(unique = true)
    String merchantName;

    String address;
    double latitude;
    double longitude;
    String imgUrl;

    @Enumerated(EnumType.STRING)
    RegistrationStatus registrationStatus;

    @ManyToMany
    Set<CuisineType> cuisineTypes;

    @PrePersist
    protected void onCreate() {
        this.registrationStatus = RegistrationStatus.PENDING;
    }
}