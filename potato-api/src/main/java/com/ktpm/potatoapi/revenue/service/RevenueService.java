package com.ktpm.potatoapi.revenue.service;

import com.ktpm.potatoapi.revenue.dto.RevenueResponse;
import com.ktpm.potatoapi.revenue.dto.RevenueTransactionResponse;
import com.ktpm.potatoapi.revenue.dto.RevenueTransactionRequest;

import java.time.LocalDate;
import java.util.List;

public interface RevenueService {
    // services for merchant admin
    RevenueResponse getMyRevenueInDateRange(LocalDate startDate, LocalDate endDate);
    List<RevenueTransactionResponse> getMyRevenueTransactionsInDateRange(LocalDate startDate, LocalDate endDate);

    // services for system admin
    RevenueTransactionResponse uploadTransactionImg(Long id, RevenueTransactionRequest request);
}
