package com.globalsafetypass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "readiness_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadinessCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tripProfileId;
    private String checkType; // "card_valid", "card_expiry", "overseas_payment", "limit", "app_notification", "backup_verification"
    private Boolean isCompleted;
    private String description;
    private String action; // 建议的操作

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
