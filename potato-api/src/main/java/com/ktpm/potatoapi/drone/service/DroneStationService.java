package com.ktpm.potatoapi.drone.service;

import com.ktpm.potatoapi.drone.dto.DroneResponse;
import com.ktpm.potatoapi.drone.dto.DroneStationRequest;
import com.ktpm.potatoapi.drone.dto.DroneStationResponse;

import java.util.List;

public interface DroneStationService {
    List<DroneStationResponse> getAllDroneStations();
    List<DroneResponse> getAllDronesOfStation(Long id);
    DroneStationResponse getDroneStation(Long id);
    DroneStationResponse createDroneStation(DroneStationRequest request);
    DroneStationResponse updateDroneStation(Long id, DroneStationRequest request);
}