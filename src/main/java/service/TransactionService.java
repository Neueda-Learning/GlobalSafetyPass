package com.hsbc.travel.service;

import com.hsbc.travel.dto.PaymentRequest;
import com.hsbc.travel.entity.*;
import com.hsbc.travel.exception.ResourceNotFoundException;
import com.hsbc.travel.repository.CardRepository;
import com.hsbc.travel.repository.TransactionRepository;
import com.hsbc.travel.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TripRepository tripRepository;
    private final CardRepository cardRepository;
    private final FailureCodeMapper failureCodeMapper;
    private final MonitoringService monitoringService;

    public TransactionService(TransactionRepository transactionRepository, TripRepository tripRepository,
                              CardRepository cardRepository, FailureCodeMapper failureCodeMapper,
                              MonitoringService monitoringService) {
        this.transactionRepository = transactionRepository;
        this.tripRepository = tripRepository;
        this.cardRepository = cardRepository;
        this.failureCodeMapper = failureCodeMapper;
        this.monitoringService = monitoringService;
    }

    public List<Transaction> getTransactionsByTrip(Long tripId) {
        return transactionRepository.findByTripIdOrderByTransactionTimeDesc(tripId);
    }

    @Transactional
    public Transaction processPayment(Long tripId, PaymentRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + tripId));
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + request.getCardId()));

        Transaction transaction = new Transaction();
        transaction.setTrip(trip);
        transaction.setCard(card);
        transaction.setMerchant(request.getMerchant());
        transaction.setMerchantLocation(request.getMerchantLocation());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency().toUpperCase());
        transaction.setExchangeRate(request.getExchangeRate() != null ? request.getExchangeRate() : BigDecimal.ONE);
        transaction.setCategory(request.getCategory());

        String failureCode = validatePayment(card, request.getAmount());
        if (failureCode != null) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureCode(failureCode);
            transaction.setFailureMessage(failureCodeMapper.getMessage(failureCode));
            transaction.setRecommendedAction(failureCodeMapper.getRecommendedAction(failureCode));
        } else {
            card.setBalance(card.getBalance().subtract(request.getAmount()));
            cardRepository.save(card);
            transaction.setStatus(TransactionStatus.SUCCESS);
        }

        transaction = transactionRepository.save(transaction);
        monitoringService.analyzeTransaction(transaction);
        return transaction;
    }

    private String validatePayment(Card card, BigDecimal amount) {
        if (card.getStatus() == CardStatus.FROZEN) {
            return "CARD_FROZEN";
        }
        if (card.getStatus() == CardStatus.EXPIRED || card.getExpiryDate().isBefore(LocalDate.now())) {
            return "CARD_EXPIRED";
        }
        if (!card.isOverseasEnabled()) {
            return "OVERSEAS_DISABLED";
        }
        if (card.getBalance().compareTo(amount) < 0) {
            return "INSUFFICIENT_BALANCE";
        }
        if (card.getDailyLimit().compareTo(amount) < 0) {
            return "LIMIT_EXCEEDED";
        }
        return null;
    }
}
