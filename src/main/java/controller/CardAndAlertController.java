package com.hsbc.travel.controller;

import com.hsbc.travel.dto.CardActionRequest;
import com.hsbc.travel.entity.Alert;
import com.hsbc.travel.entity.Card;
import com.hsbc.travel.service.CardService;
import com.hsbc.travel.service.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CardAndAlertController {

    private final CardService cardService;
    private final MonitoringService monitoringService;

    public CardAndAlertController(CardService cardService, MonitoringService monitoringService) {
        this.cardService = cardService;
        this.monitoringService = monitoringService;
    }

    @GetMapping("/cards")
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/cards/{id}")
    public Card getCard(@PathVariable Long id) {
        return cardService.getCard(id);
    }

    @PutMapping("/cards/{id}/controls")
    public Card updateCardControls(@PathVariable Long id, @RequestBody CardActionRequest request) {
        return cardService.updateCardControls(id, request);
    }

    @PostMapping("/cards/{id}/freeze")
    public Card freezeCard(@PathVariable Long id) {
        return cardService.freezeCard(id);
    }

    @PostMapping("/cards/{id}/unfreeze")
    public Card unfreezeCard(@PathVariable Long id) {
        return cardService.unfreezeCard(id);
    }

    @GetMapping("/trips/{tripId}/alerts")
    public List<Alert> getAlerts(@PathVariable Long tripId) {
        return monitoringService.getAlertsByTrip(tripId);
    }

    @PostMapping("/alerts/{alertId}/confirm")
    public ResponseEntity<Alert> confirmAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(monitoringService.confirmAlert(alertId));
    }

    @PostMapping("/alerts/{alertId}/report")
    public ResponseEntity<Alert> reportAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(monitoringService.reportAlert(alertId));
    }

    @PostMapping("/alerts/{alertId}/freeze")
    public ResponseEntity<Alert> freezeFromAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(monitoringService.freezeFromAlert(alertId));
    }
}
