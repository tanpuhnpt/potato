package com.ktpm.potatoapi.order.mapper;

import com.ktpm.potatoapi.feedback.mapper.FeedbackMapper;
import com.ktpm.potatoapi.order.dto.OrderResponse;
import com.ktpm.potatoapi.order.dto.OrderRequest;
import com.ktpm.potatoapi.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = FeedbackMapper.class)
public interface OrderMapper {
    Order toEntity(OrderRequest orderRequest);

    @Mapping(target = "orderItems", ignore = true)
    OrderResponse toResponse(Order order);
}
