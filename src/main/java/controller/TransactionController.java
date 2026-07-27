package com.hsbc.travel.controller;

import com.hsbc.travel.dto.PaymentRequest;
import com.hsbc.travel.entity.Transaction;
import com.hsbc.travel.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getTransactions(@PathVariable Long tripId) {
        return transactionService.getTransactionsByTrip(tripId);
    }

    @PostMapping
    public ResponseEntity<Transaction> processPayment(@PathVariable Long tripId,
                                                        @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(transactionService.processPayment(tripId, request));
    }
}
