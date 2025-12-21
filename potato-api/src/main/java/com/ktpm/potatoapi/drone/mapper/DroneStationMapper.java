package com.ktpm.potatoapi.drone.mapper;

import com.ktpm.potatoapi.drone.dto.DroneStationRequest;
import com.ktpm.potatoapi.drone.dto.DroneStationResponse;
import com.ktpm.potatoapi.drone.entity.DroneStation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DroneStationMapper {
    DroneStation toEntity(DroneStationRequest dto);
    DroneStationResponse toResponse(DroneStation entity);
    void update(@MappingTarget DroneStation entity, DroneStationRequest dto);
}