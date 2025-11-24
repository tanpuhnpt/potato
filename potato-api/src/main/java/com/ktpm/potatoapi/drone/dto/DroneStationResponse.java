package com.ktpm.potatoapi.drone.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DroneStationResponse {
    Long id;
    String name;
    String address;
    double latitude;
    double longitude;
}