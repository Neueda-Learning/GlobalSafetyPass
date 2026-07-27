package com.globalsafetypass.controller;

import com.globalsafetypass.dto.EmergencyWorkflowDTO;
import com.globalsafetypass.dto.ApiResponse;
import com.globalsafetypass.service.EmergencyWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyWorkflowController {
    private final EmergencyWorkflowService emergencyService;

    @PostMapping("/{tripProfileId}/card-lost")
    public ApiResponse<EmergencyWorkflowDTO> handleCardLost(@PathVariable Long tripProfileId) {
        EmergencyWorkflowDTO result = emergencyService.handleCardLost(tripProfileId);
        return ApiResponse.<EmergencyWorkflowDTO>builder()
                .code(200)
                .message("Card lost workflow initiated")
                .data(result)
                .build();
    }

    @PostMapping("/{tripProfileId}/fraud")
    public ApiResponse<EmergencyWorkflowDTO> handleFraud(
            @PathVariable Long tripProfileId,
            @RequestParam Long transactionId,
            @RequestParam String fraudType) {
        EmergencyWorkflowDTO result = emergencyService.handleFraud(tripProfileId, transactionId, fraudType);
        return ApiResponse.<EmergencyWorkflowDTO>builder()
                .code(200)
                .message("Fraud workflow initiated")
                .data(result)
                .build();
    }

    @PostMapping("/{tripProfileId}/no-verification")
    public ApiResponse<EmergencyWorkflowDTO> handleNoVerification(@PathVariable Long tripProfileId) {
        EmergencyWorkflowDTO result = emergencyService.handleNoVerification(tripProfileId);
        return ApiResponse.<EmergencyWorkflowDTO>builder()
                .code(200)
                .message("No verification workflow initiated")
                .data(result)
                .build();
    }

    @PostMapping("/workflow/{workflowId}/complete")
    public ApiResponse<EmergencyWorkflowDTO> completeWorkflow(@PathVariable Long workflowId) {
        EmergencyWorkflowDTO result = emergencyService.completeWorkflow(workflowId);
        return ApiResponse.<EmergencyWorkflowDTO>builder()
                .code(200)
                .message("Workflow completed")
                .data(result)
                .build();
    }
}
