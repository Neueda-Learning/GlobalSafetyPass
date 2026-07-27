package com.globalsafetypass.service;

import com.globalsafetypass.dto.TransactionDTO;
import com.globalsafetypass.entity.TransactionRecord;
import com.globalsafetypass.repository.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeclineExplanationService {
    private final TransactionRecordRepository transactionRepository;

    public TransactionDTO explainDecline(Long transactionId) {
        TransactionRecord record = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if ("success".equals(record.getStatus())) {
            throw new RuntimeException("Transaction was successful, no decline to explain");
        }
        
        return convertToDTO(record);
    }

    public TransactionDTO recordFailedTransaction(TransactionDTO dto) {
        TransactionRecord record = TransactionRecord.builder()
                .tripProfileId(dto.getTripProfileId())
                .cardId(dto.getCardId())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .location(dto.getLocation())
                .status(dto.getStatus())
                .declineReason(dto.getDeclineReason())
                .declineExplanation(explainReason(dto.getDeclineReason()))
                .recommendedAction(getRecommendedAction(dto.getDeclineReason()))
                .build();
        
        TransactionRecord saved = transactionRepository.save(record);
        return convertToDTO(saved);
    }

    private String explainReason(String reasonCode) {
        return switch(reasonCode) {
            case "DAILY_LIMIT_EXCEEDED" -> "您的交易超过了每日卡片限额";
            case "INSUFFICIENT_FUNDS" -> "卡片余额不足";
            case "OVERSEAS_PAYMENT_DISABLED" -> "海外支付未开启";
            case "CARD_EXPIRED" -> "您的卡片已过期";
            case "CARD_BLOCKED" -> "您的卡片已被冻结";
            case "INVALID_PIN" -> "输入的PIN码有误";
            case "TRANSACTION_NOT_PERMITTED" -> "此交易类型不被允许";
            case "ISSUER_DECLINED" -> "银行拒绝了此交易";
            default -> "交易被拒绝，原因未知";
        };
    }

    private String getRecommendedAction(String reasonCode) {
        return switch(reasonCode) {
            case "DAILY_LIMIT_EXCEEDED" -> "提高每日消费限额";
            case "INSUFFICIENT_FUNDS" -> "向卡片充值";
            case "OVERSEAS_PAYMENT_DISABLED" -> "开启海外支付";
            case "CARD_EXPIRED" -> "申请新卡";
            case "CARD_BLOCKED" -> "联系客服解冻卡片";
            case "INVALID_PIN" -> "重新输入PIN码";
            case "TRANSACTION_NOT_PERMITTED" -> "联系客服获取帮助";
            case "ISSUER_DECLINED" -> "联系银行客服";
            default -> "请联系客服获取帮助";
        };
    }

    public List<TransactionDTO> getTransactionHistory(Long tripProfileId) {
        return transactionRepository.findByTripProfileId(tripProfileId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO convertToDTO(TransactionRecord record) {
        return TransactionDTO.builder()
                .id(record.getId())
                .tripProfileId(record.getTripProfileId())
                .cardId(record.getCardId())
                .amount(record.getAmount())
                .currency(record.getCurrency())
                .location(record.getLocation())
                .status(record.getStatus())
                .declineReason(record.getDeclineReason())
                .declineExplanation(record.getDeclineExplanation())
                .recommendedAction(record.getRecommendedAction())
                .transactionTime(record.getTransactionTime())
                .build();
    }
}
