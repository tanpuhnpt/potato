package com.ktpm.potatoapi.drone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DroneStationRequest {
    @NotBlank(message = "DRONE_STATION_NAME_BLANK")
    String name;

    @NotBlank(message = "DRONE_STATION_ADDRESS_BLANK")
    String address;

    @NotNull(message = "DRONE_STATION_LATITUDE_NULL")
    double latitude;

    @NotNull(message = "DRONE_STATION_LONGITUDE_NULL")
    double longitude;
}