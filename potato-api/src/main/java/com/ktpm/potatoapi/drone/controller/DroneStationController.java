package com.ktpm.potatoapi.drone.controller;

import com.ktpm.potatoapi.drone.dto.DroneStationRequest;
import com.ktpm.potatoapi.drone.service.DroneStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drone-stations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Drone Station APIs", description = "APIs for drone station")
public class DroneStationController {
    DroneStationService service;

    @Operation(summary = "Show all drone stations",
            description = "API for System Admin to retrieve a list of all drone stations")
    @GetMapping
    public ResponseEntity<?> getAllDroneStations() {
        return ResponseEntity.ok(service.getAllDroneStations());
    }

    @GetMapping("/{id}/drones")
    @Operation(summary = "Show all drones of a station",
            description = "API for System Admin to retrieve a list of all drones of a station")
    public ResponseEntity<?> getAllDronesOfStation(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAllDronesOfStation(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show a drone station", description = "API for System Admin to retrieve a specific ")
    public ResponseEntity<?> getDroneStation(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDroneStation(id));
    }

    @PostMapping
    public ResponseEntity<?> createDroneStation(@RequestBody @Valid DroneStationRequest request) {
        return ResponseEntity.ok(service.createDroneStation(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDroneStation(@PathVariable Long id,
                                                @RequestBody @Valid DroneStationRequest request) {
        return ResponseEntity.ok(service.updateDroneStation(id, request));
    }
}