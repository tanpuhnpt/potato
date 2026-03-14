package com.ktpm.potatoapi.revenue.service;

import com.ktpm.potatoapi.cloudinary.CloudinaryService;
import com.ktpm.potatoapi.common.exception.AppException;
import com.ktpm.potatoapi.common.exception.ErrorCode;
import com.ktpm.potatoapi.merchant.service.MerchantContextProvider;
import com.ktpm.potatoapi.merchant.entity.Merchant;
import com.ktpm.potatoapi.merchant.repo.MerchantRepository;
import com.ktpm.potatoapi.revenue.dto.RevenueResponse;
import com.ktpm.potatoapi.revenue.dto.RevenueTransactionRequest;
import com.ktpm.potatoapi.revenue.dto.RevenueTransactionResponse;
import com.ktpm.potatoapi.revenue.entity.RevenueTransaction;
import com.ktpm.potatoapi.revenue.mapper.TransactionMapper;
import com.ktpm.potatoapi.revenue.repo.RevenueTransactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueServiceImpl implements RevenueService {
    MerchantContextProvider merchantContextProvider;
    RevenueTransactionRepository transactionRepository;
    MerchantRepository merchantRepository;
    CloudinaryService cloudinaryService;
    TransactionMapper mapper;

    @Override
    public RevenueResponse getMyRevenueInDateRange(LocalDate startDate, LocalDate endDate) {
        Merchant merchant = merchantContextProvider.getCurrentMerchant();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return transactionRepository.getNetRevenueByMerchantAndDateRange(merchant.getId(), startDateTime, endDateTime);
    }

    @Override
    public List<RevenueTransactionResponse> getMyRevenueTransactionsInDateRange(LocalDate startDate, LocalDate endDate) {
        Merchant merchant = merchantContextProvider.getCurrentMerchant();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return transactionRepository
                .getRevenueTransactionsByCreatedAtBetweenAndMerchant(startDateTime, endDateTime, merchant)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public RevenueTransactionResponse uploadTransactionImg(Long id, RevenueTransactionRequest request) {
        Merchant merchant = merchantRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        RevenueTransaction transaction = new RevenueTransaction();
        transaction.setMerchant(merchant);
        transaction.setDescription(request.getDescription().toUpperCase());
        transaction.setImgUrl(cloudinaryService.upload(request.getImgFile(), "revenue transaction"));

        return mapper.toResponse(transactionRepository.save(transaction));
    }
}
