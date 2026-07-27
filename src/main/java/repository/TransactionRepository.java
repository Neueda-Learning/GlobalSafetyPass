package com.hsbc.travel.repository;

import com.hsbc.travel.entity.Transaction;
import com.hsbc.travel.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTripIdOrderByTransactionTimeDesc(Long tripId);
    List<Transaction> findByTripIdAndStatus(Long tripId, TransactionStatus status);
    List<Transaction> findByMerchantAndAmountAndTripId(String merchant, java.math.BigDecimal amount, Long tripId);
}
