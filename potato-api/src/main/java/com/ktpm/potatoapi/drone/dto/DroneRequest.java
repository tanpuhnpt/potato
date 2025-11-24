package com.ktpm.potatoapi.drone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DroneRequest {
    @NotNull(message = "DRONE_STATION_NULL")
    Long stationId;

    @NotNull(message = "DRONE_CODE_NULL")
    String code;
}