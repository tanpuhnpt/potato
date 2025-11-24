package com.ktpm.potatoapi.drone.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.drone.dto.DroneResponse;
import com.ktpm.potatoapi.drone.dto.DroneStationRequest;
import com.ktpm.potatoapi.drone.dto.DroneStationResponse;
import com.ktpm.potatoapi.drone.entity.DroneStation;
import com.ktpm.potatoapi.drone.mapper.DroneMapper;
import com.ktpm.potatoapi.drone.mapper.DroneStationMapper;
import com.ktpm.potatoapi.drone.repo.DroneRepository;
import com.ktpm.potatoapi.drone.repo.DroneStationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DroneStationServiceImpl implements DroneStationService {
    DroneStationRepository droneStationRepository;
    DroneStationMapper stationMapper;
    DroneRepository droneRepository;
    DroneMapper droneMapper;

    @Override
    public List<DroneStationResponse> getAllDroneStations() {
        return droneStationRepository.findAll()
                .stream()
                .map(stationMapper::toResponse)
                .toList();
    }

    @Override
    public List<DroneResponse> getAllDronesOfStation(Long id) {
        return droneRepository.findAllByStationIdAndIsActiveTrue(id)
                .stream()
                .map(droneMapper::toResponse)
                .toList();
    }

    @Override
    public DroneStationResponse getDroneStation(Long id) {
        DroneStation station = droneStationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DRONE_STATION_NOT_FOUND));
        return stationMapper.toResponse(station);
    }

    @Override
    public DroneStationResponse createDroneStation(DroneStationRequest request) {
        DroneStation station = stationMapper.toEntity(request);

        try {
            return stationMapper.toResponse(droneStationRepository.save(station));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.DRONE_STATION_EXISTED);
        }
    }

    @Override
    public DroneStationResponse updateDroneStation(Long id, DroneStationRequest request) {
        DroneStation station = droneStationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DRONE_STATION_NOT_FOUND));

        stationMapper.update(station, request);

        try {
            return stationMapper.toResponse(droneStationRepository.save(station));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.DRONE_STATION_EXISTED);
        }    }
}