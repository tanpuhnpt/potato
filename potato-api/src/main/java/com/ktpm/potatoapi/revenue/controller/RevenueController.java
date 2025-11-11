package com.ktpm.potatoapi.revenue.controller;

import com.ktpm.potatoapi.revenue.dto.RevenueTransactionRequest;
import com.ktpm.potatoapi.revenue.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "Revenue APIs", description = "APIs for revenue")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin("*")
public class RevenueController {
    RevenueService revenueService;

    @GetMapping("/merchant/revenue/overview")
    @Operation(summary = "Show overview of my merchant's revenue",
            description = "API for Merchant Admin to retrieve an overview of their own merchant's revenue in date range")
    public ResponseEntity<?> getMyRevenueInDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(revenueService.getMyRevenueInDateRange(startDate, endDate));
    }

    @GetMapping("/merchant/revenue/transaction")
    @Operation(summary = "Show my merchant's transaction",
            description = "API for Merchant Admin to retrieve history of their own merchant's revenue transaction in date range")
    public ResponseEntity<?> getMyRevenueTransactionsInDateRange(@RequestParam LocalDate startDate,
                                                                 @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok(revenueService.getMyRevenueTransactionsInDateRange(startDate, endDate));
    }

    @PostMapping(value = "/admin/revenue/merchants/{id}/upload-transaction-proof",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload transaction image",
            description = "API for Merchant Admin to upload revenue transaction image")
    public ResponseEntity<?> uploadTransactionImg(@PathVariable Long id, RevenueTransactionRequest request) {
        return ResponseEntity.ok(revenueService.uploadTransactionImg(id, request));
    }
}
