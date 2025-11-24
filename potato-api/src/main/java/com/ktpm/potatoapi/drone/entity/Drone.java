package com.ktpm.potatoapi.drone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String code;

    double latitude;
    double longitude;
    int battery;

    @Enumerated(EnumType.STRING)
    DroneStatus status;

    boolean isActive;

    @ManyToOne
    DroneStation station;

    @PrePersist
    protected void onCreate() {
        this.battery = 100;
        this.status = DroneStatus.AVAILABLE;
        this.isActive = true;
    }
}