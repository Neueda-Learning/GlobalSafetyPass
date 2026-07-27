package com.hsbc.travel.service;

import com.hsbc.travel.entity.*;
import com.hsbc.travel.repository.AlertRepository;
import com.hsbc.travel.repository.CardRepository;
import com.hsbc.travel.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MonitoringService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("500");

    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public MonitoringService(AlertRepository alertRepository, TransactionRepository transactionRepository,
                             CardRepository cardRepository) {
        this.alertRepository = alertRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void analyzeTransaction(Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.SUCCESS) {
            return;
        }

        Trip trip = transaction.getTrip();
        LocalDate txDate = transaction.getTransactionTime().toLocalDate();

        if (txDate.isBefore(trip.getStartDate()) || txDate.isAfter(trip.getEndDate())) {
            createAlert(transaction, trip,
                    "Transaction occurred outside trip dates (" + trip.getStartDate() + " to " + trip.getEndDate() + ").",
                    RiskLevel.HIGH);
        }

        if (transaction.getMerchantLocation() != null
                && !transaction.getMerchantLocation().equalsIgnoreCase(trip.getDestination())) {
            createAlert(transaction, trip,
                    "Transaction location (" + transaction.getMerchantLocation() + ") does not match trip destination (" + trip.getDestination() + ").",
                    RiskLevel.HIGH);
        }

        List<Transaction> duplicates = transactionRepository.findByMerchantAndAmountAndTripId(
                transaction.getMerchant(), transaction.getAmount(), trip.getId());
        if (duplicates.size() > 1) {
            createAlert(transaction, trip,
                    "Possible duplicate charge detected at " + transaction.getMerchant() + " for " + transaction.getAmount() + ".",
                    RiskLevel.MEDIUM);
        }

        if ("ATM Withdrawal".equalsIgnoreCase(transaction.getCategory())
                && transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0) {
            createAlert(transaction, trip,
                    "Unusual high-value ATM withdrawal of " + transaction.getAmount() + " " + transaction.getCurrency() + ".",
                    RiskLevel.HIGH);
        }

        if (transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0
                && !"ATM Withdrawal".equalsIgnoreCase(transaction.getCategory())) {
            createAlert(transaction, trip,
                    "High-value transaction of " + transaction.getAmount() + " " + transaction.getCurrency() + " at " + transaction.getMerchant() + ".",
                    RiskLevel.MEDIUM);
        }
    }

    private void createAlert(Transaction transaction, Trip trip, String reason, RiskLevel riskLevel) {
        boolean exists = alertRepository.findByTripIdOrderByCreatedAtDesc(trip.getId()).stream()
                .anyMatch(a -> a.getTransaction().getId().equals(transaction.getId())
                        && a.getReason().equals(reason));
        if (!exists) {
            Alert alert = new Alert();
            alert.setTransaction(transaction);
            alert.setTrip(trip);
            alert.setReason(reason);
            alert.setRiskLevel(riskLevel);
            alert.setStatus(AlertStatus.PENDING);
            alertRepository.save(alert);
        }
    }

    public List<Alert> getAlertsByTrip(Long tripId) {
        return alertRepository.findByTripIdOrderByCreatedAtDesc(tripId);
    }

    @Transactional
    public Alert confirmAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.setStatus(AlertStatus.CONFIRMED);
        return alertRepository.save(alert);
    }

    @Transactional
    public Alert reportAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.setStatus(AlertStatus.REPORTED);
        return alertRepository.save(alert);
    }

    @Transactional
    public Alert freezeFromAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        Card card = alert.getTransaction().getCard();
        card.setStatus(CardStatus.FROZEN);
        cardRepository.save(card);
        alert.setStatus(AlertStatus.FROZEN);
        return alertRepository.save(alert);
    }
}
