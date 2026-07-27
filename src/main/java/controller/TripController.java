package com.hsbc.travel.controller;

import com.hsbc.travel.dto.TripCreateRequest;
import com.hsbc.travel.entity.Trip;
import com.hsbc.travel.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public Trip getTrip(@PathVariable Long id) {
        return tripService.getTrip(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTrip(@Valid @RequestBody TripCreateRequest request) {
        return ResponseEntity.ok(tripService.createTrip(request));
    }

    @GetMapping("/{id}/readiness")
    public ResponseEntity<?> getReadiness(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getReadiness(id));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getDashboard(id));
    }
}
