package com.ktpm.potatoapi.drone.mapper;

import com.ktpm.potatoapi.drone.dto.DroneResponse;
import com.ktpm.potatoapi.drone.entity.Drone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DroneMapper {
    DroneResponse toResponse(Drone entity);
}