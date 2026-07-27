package com.hsbc.travel.service;

import com.hsbc.travel.dto.ReadinessResult;
import com.hsbc.travel.dto.TripCreateRequest;
import com.hsbc.travel.dto.TripDashboard;
import com.hsbc.travel.entity.Card;
import com.hsbc.travel.entity.Trip;
import com.hsbc.travel.entity.TransactionStatus;
import com.hsbc.travel.exception.ResourceNotFoundException;
import com.hsbc.travel.repository.AlertRepository;
import com.hsbc.travel.repository.CardRepository;
import com.hsbc.travel.repository.TransactionRepository;
import com.hsbc.travel.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final ReadinessService readinessService;

    public TripService(TripRepository tripRepository, CardRepository cardRepository,
                       TransactionRepository transactionRepository, AlertRepository alertRepository,
                       ReadinessService readinessService) {
        this.tripRepository = tripRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.alertRepository = alertRepository;
        this.readinessService = readinessService;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip getTrip(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
    }

    @Transactional
    public Map<String, Object> createTrip(TripCreateRequest request) {
        Card card = cardRepository.findById(request.getPreferredCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + request.getPreferredCardId()));

        Trip trip = new Trip();
        trip.setDestination(request.getDestination());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setBudget(request.getBudget());
        trip.setCurrency(request.getCurrency().toUpperCase());
        trip.setPreferredCard(card);

        ReadinessResult readiness = readinessService.evaluate(trip, card);
        trip.setReadinessScore(BigDecimal.valueOf(readiness.getScore()));
        trip = tripRepository.save(trip);

        Map<String, Object> response = new HashMap<>();
        response.put("trip", trip);
        response.put("readiness", readiness);
        return response;
    }

    public ReadinessResult getReadiness(Long tripId) {
        Trip trip = getTrip(tripId);
        return readinessService.evaluate(trip, trip.getPreferredCard());
    }

    public TripDashboard getDashboard(Long tripId) {
        Trip trip = getTrip(tripId);
        BigDecimal totalSpent = transactionRepository.findByTripIdAndStatus(tripId, TransactionStatus.SUCCESS)
                .stream()
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TripDashboard dashboard = new TripDashboard();
        dashboard.setTripId(tripId);
        dashboard.setDestination(trip.getDestination());
        dashboard.setBudget(trip.getBudget());
        dashboard.setTotalSpent(totalSpent);
        dashboard.setRemainingBudget(trip.getBudget().subtract(totalSpent));
        dashboard.setCurrency(trip.getCurrency());
        dashboard.setTransactionCount(transactionRepository.findByTripIdOrderByTransactionTimeDesc(tripId).size());
        dashboard.setAlertCount(alertRepository.findByTripIdOrderByCreatedAtDesc(tripId).size());
        return dashboard;
    }
}
