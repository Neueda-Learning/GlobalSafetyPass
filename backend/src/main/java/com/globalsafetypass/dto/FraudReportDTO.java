package com.globalsafetypass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudReportDTO {
    private Long id;
    private Long transactionId;
    private Long tripProfileId;
    private String fraudType;
    private String status;
    private String description;
}
