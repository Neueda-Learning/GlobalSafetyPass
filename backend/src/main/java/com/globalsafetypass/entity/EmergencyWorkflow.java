package com.globalsafetypass.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_workflow")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyWorkflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tripProfileId;
    private String workflowType; // "card_lost", "fraud", "no_verification", "payment_failure"
    private String status; // "initiated", "in_progress", "completed"
    private String cardAction; // "frozen", "replaced", "none"
    private String fraudAction; // "marked_unauthorized", "disputed", "none"
    private Boolean cardReplaced;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
