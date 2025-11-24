package com.ktpm.potatoapi.drone.repo;

import com.ktpm.potatoapi.drone.entity.DroneStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneStationRepository extends JpaRepository<DroneStation, Long> {
}