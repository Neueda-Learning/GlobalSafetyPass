package com.globalsafetypass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyWorkflowDTO {
    private Long id;
    private Long tripProfileId;
    private String workflowType;
    private String status;
    private String cardAction;
    private String fraudAction;
    private Boolean cardReplaced;
}
