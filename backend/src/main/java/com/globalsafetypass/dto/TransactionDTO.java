package com.globalsafetypass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private Long tripProfileId;
    private String cardId;
    private BigDecimal amount;
    private String currency;
    private String location;
    private String status;
    private String declineReason;
    private String declineExplanation;
    private String recommendedAction;
    private LocalDateTime transactionTime;
}
