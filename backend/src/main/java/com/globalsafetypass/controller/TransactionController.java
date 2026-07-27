package com.globalsafetypass.controller;

import com.globalsafetypass.dto.TransactionDTO;
import com.globalsafetypass.dto.ApiResponse;
import com.globalsafetypass.service.DeclineExplanationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final DeclineExplanationService declineService;

    @PostMapping("/record-failed")
    public ApiResponse<TransactionDTO> recordFailedTransaction(@RequestBody TransactionDTO dto) {
        TransactionDTO result = declineService.recordFailedTransaction(dto);
        return ApiResponse.<TransactionDTO>builder()
                .code(200)
                .message("Failed transaction recorded")
                .data(result)
                .build();
    }

    @GetMapping("/{transactionId}/decline-reason")
    public ApiResponse<TransactionDTO> getDeclineReason(@PathVariable Long transactionId) {
        TransactionDTO result = declineService.explainDecline(transactionId);
        return ApiResponse.<TransactionDTO>builder()
                .code(200)
                .message("Decline reason explained")
                .data(result)
                .build();
    }

    @GetMapping("/history/{tripProfileId}")
    public ApiResponse<List<TransactionDTO>> getTransactionHistory(@PathVariable Long tripProfileId) {
        List<TransactionDTO> result = declineService.getTransactionHistory(tripProfileId);
        return ApiResponse.<List<TransactionDTO>>builder()
                .code(200)
                .message("Transaction history retrieved")
                .data(result)
                .build();
    }
}
