package com.globalsafetypass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripProfileDTO {
    private Long id;
    private String userId;
    private String destination;
    private Date departureDate;
    private Date returnDate;
    private String travelType;
    private String cardId;
    private Integer readinessScore;
    private List<ReadinessCheckDTO> readinessChecks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
