package com.ktpm.potatoapi.order.service;

import com.ktpm.potatoapi.cart.dto.CartItemRequest;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.common.pagination.PageResponse;
import com.ktpm.potatoapi.common.utils.SecurityUtils;
import com.ktpm.potatoapi.drone.entity.Drone;
import com.ktpm.potatoapi.drone.entity.DroneStation;
import com.ktpm.potatoapi.drone.entity.DroneStatus;
import com.ktpm.potatoapi.drone.repo.DroneRepository;
import com.ktpm.potatoapi.drone.repo.DroneStationRepository;
import com.ktpm.potatoapi.menu.entity.MenuItem;
import com.ktpm.potatoapi.menu.repo.MenuItemRepository;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.option.entity.Option;
import com.ktpm.potatoapi.option.entity.OptionValue;
import com.ktpm.potatoapi.option.repo.OptionValueRepository;
import com.ktpm.potatoapi.order.dto.*;
import com.ktpm.potatoapi.order.entity.Order;
import com.ktpm.potatoapi.order.entity.OrderItem;
import com.ktpm.potatoapi.order.entity.OrderItemOptionValue;
import com.ktpm.potatoapi.order.entity.OrderStatus;
import com.ktpm.potatoapi.order.mapper.OrderItemMapper;
import com.ktpm.potatoapi.order.mapper.OrderItemOptionValueMapper;
import com.ktpm.potatoapi.order.mapper.OrderMapper;
import com.ktpm.potatoapi.order.repo.OrderItemOptionValueRepository;
import com.ktpm.potatoapi.order.repo.OrderItemRepository;
import com.ktpm.potatoapi.order.repo.OrderRepository;
import com.ktpm.potatoapi.user.entity.User;
import com.ktpm.potatoapi.user.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
    UserRepository userRepository;
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    MenuItemRepository menuItemRepository;
    OptionValueRepository optionValueRepository;
    SecurityUtils securityUtils;
    OrderItemOptionValueRepository orderItemOptionValueRepository;
    OrderItemMapper orderItemMapper;
    OrderItemOptionValueMapper orderItemOptionValueMapper;
    DroneRepository droneRepository;
    DroneStationRepository droneStationRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User customer = userRepository.findByEmail(securityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // get merchant from menu item
        Merchant merchant = getMerchantFromMenuItemId(orderRequest.getCartItems().get(0).getMenuItemId());

        // build order
        Order order = buildOrder(orderRequest, customer, merchant);
        orderRepository.save(order); // lưu để tạo id

        // calc total
        long total = calculateTotal(orderRequest, order);
        long deliveryFee = 15000L;
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(total + deliveryFee);
        orderRepository.save(order);

        log.info("Order from {} created", customer.getEmail());

        List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());
        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getAllOrdersInProgress() {
        User customer = userRepository.findByEmail(securityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<OrderResponse> orderResponses = new ArrayList<>();
        List<Order> orders = orderRepository.getOrderInProgressByCustomer(customer.getId());
        for(Order order : orders) {
            List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());
            OrderResponse orderResponse = orderMapper.toResponse(order);
            orderResponse.setOrderItems(orderItemResponses);
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    @Override
    public PageResponse<OrderResponse> getOrderHistory(int page, int size) {
        User customer = userRepository.findByEmail(securityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OrderResponse> responsePage = orderRepository
                .getOrderHistoryByCustomer(customer.getId(), pageable)
                .map(order -> {
                    List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());
                    OrderResponse response = orderMapper.toResponse(order);
                    response.setOrderItems(orderItemResponses);
                    return response;
                });

        return PageResponse.from(responsePage);
    }

    @Override
    public OrderResponse confirmOrderCompleted(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // if current status is CANCELED, then it is not updatable
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.CANCELED)
            throw new AppException(ErrorCode.ORDER_STATUS_INVALID_FOR_UPDATE);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        log.info("Confirm {} completed successfully", order.getId());

        List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());
        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());

        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getAllOrdersOfMyMerchant() {
        Merchant merchant = securityUtils.getCurrentMerchant();
        return orderRepository.findAllByMerchant(merchant)
                .stream()
                .map(order -> {
                    List<OrderItemResponse> orderItemResponses =
                            mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());

                    OrderResponse orderResponse = orderMapper.toResponse(order);
                    orderResponse.setOrderItems(orderItemResponses);

                    return orderResponse;
                })
                .toList();
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // validate merchant ownership
        Merchant merchant = securityUtils.getCurrentMerchant();
        if (!order.getMerchant().equals(merchant))
            throw new AppException(ErrorCode.MUST_BE_OWNED_OF_CURRENT_MERCHANT);

        // parse order status request
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.ORDER_STATUS_REQUEST_INVALID);
        }

        // if current status is CANCELED, then it is not updatable
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.CANCELED)
            throw new AppException(ErrorCode.ORDER_STATUS_INVALID_FOR_UPDATE);

        // handle order status progress
        if (newStatus.getLevel() != currentStatus.getLevel() + 1)
            throw new AppException(ErrorCode.ORDER_STATUS_NOT_STEP_BY_STEP);

        // handle case: cancel order
        if (newStatus == OrderStatus.CANCELED)
            handleCancelOrder(order, request);

        // handle case: ready to pick up
        if (newStatus == OrderStatus.READY)
            assignDroneForOrder(order, merchant);

        order.setStatus(newStatus);
        orderRepository.save(order);

        log.info("Updated order {} status: {} successfully", order.getId(), newStatus);

        List<OrderItemResponse> orderItemResponses = mapOrderItemsWithOptionValuesToResponse(order.getOrderItems());
        OrderResponse orderResponse = orderMapper.toResponse(order);
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    private Merchant getMerchantFromMenuItemId(Long menuItemId) {
        MenuItem firstMenuItem = menuItemRepository.findByIdAndIsVisibleTrue(menuItemId)
                .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));
        return firstMenuItem.getMerchant();
    }

    private Order buildOrder(OrderRequest orderRequest, User customer, Merchant merchant) {
        Order order = orderMapper.toEntity(orderRequest); // fullName, phone, deliveryAddress, note
        order.setCustomer(customer);
        order.setMerchant(merchant);
        return order;
    }

    private long calculateTotal(OrderRequest orderRequest, Order order) {
        long total = 0L;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemRequest cartItem : orderRequest.getCartItems()) {
            MenuItem menuItem = menuItemRepository.findByIdAndIsVisibleTrue(cartItem.getMenuItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.MENU_ITEM_NOT_FOUND));

            OrderItem orderItem = buildOrderItem(cartItem, menuItem, order);
            orderItemRepository.save(orderItem); // lưu để tạo id

            // tính subtotal của mỗi item
            long subtotal = calculateSubtotal(menuItem, cartItem, orderItem);
            orderItem.setSubtotal(subtotal);
            orderItemRepository.save(orderItem);

            total += subtotal; // Tổng tiền chưa tính phí giao hàng
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        return total;
    }

    private OrderItem buildOrderItem(CartItemRequest cartItem, MenuItem menuItem, Order order) {
        return OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .note(cartItem.getNote())
                .menuItem(menuItem)
                .menuItemName(menuItem.getName())
                .menuItemBasePrice(menuItem.getBasePrice())
                .order(order)
                .build();
    }

    private long calculateSubtotal(MenuItem menuItem, CartItemRequest cartItem, OrderItem orderItem) {
        long subtotal = menuItem.getBasePrice();
        List<Long> selectedOptionValueIds = cartItem.getOptionValueIds();
        if (selectedOptionValueIds != null && !selectedOptionValueIds.isEmpty()) {
            List<OptionValue> optionValues = optionValueRepository.findAllByIdInAndIsVisibleTrue(selectedOptionValueIds);
            if (optionValues.size() != selectedOptionValueIds.size())
                throw new AppException(ErrorCode.OPTION_VALUE_NOT_FOUND);

            // set để lưu optionId đã có (đối với required option)
            Set<Long> seenRequiredOptions = new HashSet<>();

            for (OptionValue optionValue : optionValues) {
                Option parentOption = optionValue.getOption();

                // kiểm tra option có liên kết với menuItem không
                boolean belongsToMenuItem = menuItem.getOptions()
                        .stream()
                        .anyMatch(opt -> opt.getId().equals(parentOption.getId()));
                if (!belongsToMenuItem) throw new AppException(ErrorCode.MENU_ITEM_NOT_ASSIGNED_TO_OPTION);

                // kiểm tra nếu có nhiều hơn 1 option value trong 1 option có isRequired = true
                if (parentOption.isRequired()) {
                    if (seenRequiredOptions.contains(parentOption.getId()))
                        throw new AppException(ErrorCode.ORDER_HAS_MULTIPLE_OPTION_VALUES_FOR_REQUIRED_OPTION);

                    seenRequiredOptions.add(parentOption.getId());
                }

                subtotal += optionValue.getExtraPrice();

                orderItemOptionValueRepository.save(buildOrderItemOptionValue(orderItem, optionValue));
            }
        }

        return subtotal * cartItem.getQuantity();
    }

    private OrderItemOptionValue buildOrderItemOptionValue(OrderItem orderItem, OptionValue optionValue) {
        return OrderItemOptionValue.builder()
                .orderItem(orderItem)
                .optionValue(optionValue)
                .optionValueName(optionValue.getName())
                .extraPrice(optionValue.getExtraPrice())
                .build();
    }

    private List<OrderItemResponse> mapOrderItemsWithOptionValuesToResponse(List<OrderItem> orderItems) {
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            List<OrderItemOptionValue> orderItemOptionValues = orderItemOptionValueRepository.findAllByOrderItem(orderItem);
            List<OrderItemOptionValueResponse> orderItemOptionValueResponses = new ArrayList<>();

            for (OrderItemOptionValue orderItemOptionValue : orderItemOptionValues) {
                OrderItemOptionValueResponse orderItemOptionValueResponse = orderItemOptionValueMapper.toResponse(orderItemOptionValue);
                orderItemOptionValueResponses.add(orderItemOptionValueResponse);
            }

            OrderItemResponse orderItemResponse = orderItemMapper.toResponse(orderItem);
            orderItemResponse.setOptionValues(orderItemOptionValueResponses);
            orderItemResponses.add(orderItemResponse);
        }

        return orderItemResponses;
    }

    private void handleCancelOrder(Order order, OrderStatusUpdateRequest request) {
        String reason = request.getCancelReason();
        if (reason == null || reason.isBlank())
            throw new AppException(ErrorCode.CANCEL_REASON_EMPTY);
        order.setCancelReason(reason);
    }

    private void assignDroneForOrder(Order order, Merchant merchant) {
        double merchantLat = merchant.getLatitude();
        double merchantLng = merchant.getLongitude();
        double customerLat = order.getLatitude();
        double customerLng = order.getLongitude();
        double batteryConsumptionPerKm = 1.5;

        // lấy tất cả trạm và xếp tăng dần theo khoảng cách từ trạm đến cửa hàng
        List<DroneStation> stations = droneStationRepository.findAll()
                .stream()
                .sorted(Comparator.comparingDouble(station ->
                        distanceInKm(merchantLat, merchantLng, station.getLatitude(), station.getLongitude()))
                )
                .toList();

        // các bước chọn drone để giao đơn
        Drone chosenDrone = findSuitableDrone(
                stations, merchantLat, merchantLng, customerLat, customerLng, batteryConsumptionPerKm);

        // nếu không tìm được drone đủ pin ở tất cả trạm
        if (chosenDrone == null)
            throw new AppException(ErrorCode.NO_DRONE_WITH_ENOUGH_BATTERY);

        // cập nhật drone
        chosenDrone.setStatus(DroneStatus.TO_PICKUP);
        droneRepository.save(chosenDrone);

        order.setDrone(chosenDrone);

        log.info("Assigned drone {} to order {}", chosenDrone.getId(), order.getId());
    }

    private double distanceInKm(double startLat, double startLng, double endLat, double endLng) {
        int R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private Drone findSuitableDrone(
            List<DroneStation> stations,
            double merchantLat, double merchantLng,
            double customerLat, double customerLng,
            double batteryConsumptionPerKm
    ) {
        for (DroneStation station : stations) {
            List<Drone> availableDrones = droneRepository
                    .findAllByStationIdAndStatus(station.getId(), DroneStatus.AVAILABLE);

            // nếu trạm không có drone nào available thì tìm trạm kế tiếp
            if (availableDrones.isEmpty()) continue;

            // tính lượng pin cần thiết
            double requiredBattery = calculateRequiredBattery(
                    station, merchantLat, merchantLng, customerLat, customerLng, batteryConsumptionPerKm);
            log.info("requiredBattery: " + requiredBattery);

            // tìm drone đủ pin
            List<Drone> dronesEnoughBattery = availableDrones
                    .stream()
                    .filter(d -> d.getBattery() >= requiredBattery)
                    .toList();

            // nếu trạm không có drone nào đủ pin thì tìm trạm kế tiếp
            if (dronesEnoughBattery.isEmpty()) continue;

            // chọn drone có lượng pin gần nhất với lượng pin cần thiết
            return dronesEnoughBattery
                    .stream()
                    .min(Comparator.comparingDouble(d -> d.getBattery() - requiredBattery))
                    .get();

        }
        return null;
    }

    private double calculateRequiredBattery(
            DroneStation station,
            double merchantLat, double merchantLng,
            double customerLat, double customerLng,
            double consumptionPerKm
    ) {
        double stationToMerchant = distanceInKm(
                station.getLatitude(), station.getLongitude(), merchantLat, merchantLng);
        System.out.println("stationToMerchant: " + stationToMerchant);

        double merchantToCustomer = distanceInKm(
                merchantLat, merchantLng, customerLat, customerLng);
        System.out.println("merchantToCustomer: " + merchantToCustomer);

        double customerToStation = distanceInKm(
                customerLat, customerLng, station.getLatitude(), station.getLongitude());
        System.out.println("customerToStation: " + customerToStation);

        double totalDistance = stationToMerchant + merchantToCustomer + customerToStation;
        System.out.println("totalDistance: " + totalDistance);

        return totalDistance * consumptionPerKm;
    }
}
