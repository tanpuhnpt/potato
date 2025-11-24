package com.ktpm.potatoapi.drone.service;

import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.drone.dto.DroneRequest;
import com.ktpm.potatoapi.drone.dto.DroneResponse;
import com.ktpm.potatoapi.drone.entity.Drone;
import com.ktpm.potatoapi.drone.entity.DroneStation;
import com.ktpm.potatoapi.drone.entity.DroneStatus;
import com.ktpm.potatoapi.drone.mapper.DroneMapper;
import com.ktpm.potatoapi.drone.repo.DroneRepository;
import com.ktpm.potatoapi.drone.repo.DroneStationRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import com.ktpm.potatoapi.order.repo.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DroneServiceImpl implements DroneService {
    DroneRepository droneRepository;
    DroneMapper mapper;
    DroneStationRepository droneStationRepository;
    OrderRepository orderRepository;

    @Override
    public DroneResponse getDrone(Long id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.DRONE_NOT_FOUND));
        return mapper.toResponse(drone);
    }

    @Override
    public DroneResponse createDrone(DroneRequest request) {
        DroneStation station = droneStationRepository.findById(request.getStationId())
                .orElseThrow(() -> new AppException(ErrorCode.DRONE_EXISTED));

        Drone drone = new Drone();
        drone.setCode(request.getCode());
        drone.setStation(station);

        try {
            return mapper.toResponse(droneRepository.save(drone));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.DRONE_EXISTED);
        }
    }

    @Override
    public DroneResponse updateDroneLocation(Long id, double latitude, double longitude) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DRONE_NOT_FOUND));

        drone.setLatitude(latitude);
        drone.setLongitude(longitude);

        Order order = orderRepository.findByDroneIdAndStatus(drone.getId(), OrderStatus.READY);

        if (order != null) {
            Merchant merchant = order.getMerchant();

            // drone arrive at merchant
            if (Double.compare(latitude, merchant.getLatitude()) == 0 &&
                    Double.compare(longitude, merchant.getLongitude()) == 0) {

                drone.setStatus(DroneStatus.DELIVERING);
                order.setStatus(OrderStatus.DELIVERING);
                orderRepository.save(order);
            }

            // drone arrive at delivery address
            if (Double.compare(latitude, order.getLatitude()) == 0 &&
                    Double.compare(longitude, order.getLongitude()) == 0) {

                drone.setStatus(DroneStatus.RETURNING);
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }

            // drone arrive at station
            if (Double.compare(latitude, order.getLatitude()) == 0 &&
                    Double.compare(longitude, order.getLongitude()) == 0) {

                drone.setStatus(DroneStatus.AVAILABLE);
            }
        }

        return mapper.toResponse(droneRepository.save(drone));
    }

    @Override
    public void deleteDrone(Long id) {
        Drone drone = droneRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.DRONE_NOT_FOUND));
        drone.setActive(false);
        droneRepository.save(drone);
    }
}