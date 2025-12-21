package com.ktpm.potatoapi.order.mapper;

import com.ktpm.potatoapi.order.dto.OrderItemResponse;
import com.ktpm.potatoapi.order.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse toResponse(OrderItem orderItem);
}