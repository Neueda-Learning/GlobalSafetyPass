package com.globalsafetypass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tripProfileId;
    private String cardId;
    private BigDecimal amount;
    private String currency;
    private String location;
    private String status; // "success", "failed", "pending"
    private String declineReason; // 失败原因代码
    private String declineExplanation; // 用户可理解的失败原因
    private String recommendedAction; // 推荐操作

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
    }
}
