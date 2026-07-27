package com.globalsafetypass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadinessCheckDTO {
    private Long id;
    private Long tripProfileId;
    private String checkType;
    private Boolean isCompleted;
    private String description;
    private String action;
}
