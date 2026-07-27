package com.globalsafetypass.service;

import com.globalsafetypass.dto.FraudReportDTO;
import com.globalsafetypass.dto.EmergencyWorkflowDTO;
import com.globalsafetypass.entity.FraudReport;
import com.globalsafetypass.entity.EmergencyWorkflow;
import com.globalsafetypass.entity.TransactionRecord;
import com.globalsafetypass.repository.FraudReportRepository;
import com.globalsafetypass.repository.EmergencyWorkflowRepository;
import com.globalsafetypass.repository.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmergencyWorkflowService {
    private final EmergencyWorkflowRepository emergencyRepository;
    private final FraudReportRepository fraudRepository;
    private final TransactionRecordRepository transactionRepository;

    public EmergencyWorkflowDTO handleCardLost(Long tripProfileId) {
        EmergencyWorkflow workflow = EmergencyWorkflow.builder()
                .tripProfileId(tripProfileId)
                .workflowType("card_lost")
                .status("in_progress")
                .cardAction("frozen")
                .cardReplaced(false)
                .build();
        
        EmergencyWorkflow saved = emergencyRepository.save(workflow);
        return convertToDTO(saved);
    }

    public EmergencyWorkflowDTO handleFraud(Long tripProfileId, Long transactionId, String fraudType) {
        TransactionRecord transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // 创建欺诈报告
        FraudReport fraud = FraudReport.builder()
                .transactionId(transactionId)
                .tripProfileId(tripProfileId)
                .fraudType(fraudType)
                .status("reported")
                .description("用户在海外标记为非本人交易")
                .build();
        fraudRepository.save(fraud);

        // 创建紧急工作流
        EmergencyWorkflow workflow = EmergencyWorkflow.builder()
                .tripProfileId(tripProfileId)
                .workflowType("fraud")
                .status("in_progress")
                .cardAction("frozen")
                .fraudAction("marked_unauthorized")
                .cardReplaced(false)
                .build();
        
        EmergencyWorkflow saved = emergencyRepository.save(workflow);
        return convertToDTO(saved);
    }

    public EmergencyWorkflowDTO handleNoVerification(Long tripProfileId) {
        EmergencyWorkflow workflow = EmergencyWorkflow.builder()
                .tripProfileId(tripProfileId)
                .workflowType("no_verification")
                .status("in_progress")
                .cardAction("none")
                .build();
        
        EmergencyWorkflow saved = emergencyRepository.save(workflow);
        return convertToDTO(saved);
    }

    public EmergencyWorkflowDTO completeWorkflow(Long workflowId) {
        EmergencyWorkflow workflow = emergencyRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        workflow.setStatus("completed");
        if ("card_lost".equals(workflow.getWorkflowType())) {
            workflow.setCardReplaced(true);
        }
        
        EmergencyWorkflow saved = emergencyRepository.save(workflow);
        return convertToDTO(saved);
    }

    private EmergencyWorkflowDTO convertToDTO(EmergencyWorkflow workflow) {
        return EmergencyWorkflowDTO.builder()
                .id(workflow.getId())
                .tripProfileId(workflow.getTripProfileId())
                .workflowType(workflow.getWorkflowType())
                .status(workflow.getStatus())
                .cardAction(workflow.getCardAction())
                .fraudAction(workflow.getFraudAction())
                .cardReplaced(workflow.getCardReplaced())
                .build();
    }
}
