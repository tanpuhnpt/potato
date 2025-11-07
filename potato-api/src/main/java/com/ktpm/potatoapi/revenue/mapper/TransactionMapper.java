package com.ktpm.potatoapi.revenue.mapper;

import com.ktpm.potatoapi.revenue.dto.RevenueTransactionResponse;
import com.ktpm.potatoapi.revenue.entity.RevenueTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    RevenueTransactionResponse toResponse(RevenueTransaction entity);
}
