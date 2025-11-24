package com.ktpm.potatoapi.drone.repo;

import com.ktpm.potatoapi.drone.entity.Drone;
import com.ktpm.potatoapi.drone.entity.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DroneRepository extends JpaRepository<Drone, Long> {
    List<Drone> findAllByStationIdAndIsActiveTrue(Long stationId);

    List<Drone> findAllByStationIdAndStatus(Long stationId, DroneStatus status);
}