package com.ktpm.potatoapi.drone.dto;

import com.ktpm.potatoapi.drone.entity.DroneStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DroneResponse {
    Long id;
    String code;
    Integer battery;
    DroneStatus status;
    boolean isActive;
    DroneStationResponse station;
}