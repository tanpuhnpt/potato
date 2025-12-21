package com.ktpm.potatoapi.order.mapper;

import com.ktpm.potatoapi.order.dto.OrderItemOptionValueResponse;
import com.ktpm.potatoapi.order.entity.OrderItemOptionValue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemOptionValueMapper {
    OrderItemOptionValueResponse toResponse(OrderItemOptionValue orderItemOptionValue);
}
