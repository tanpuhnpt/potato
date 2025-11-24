package com.ktpm.potatoapi.drone.service;

import com.ktpm.potatoapi.drone.dto.DroneRequest;
import com.ktpm.potatoapi.drone.dto.DroneResponse;

public interface DroneService {
    DroneResponse getDrone(Long id);
    DroneResponse createDrone(DroneRequest request);
    DroneResponse updateDroneLocation(Long id, double latitude, double longitude);
    void deleteDrone(Long id);
}