package com.globalsafetypass.controller;

import com.globalsafetypass.dto.TripProfileDTO;
import com.globalsafetypass.dto.ApiResponse;
import com.globalsafetypass.service.TripProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripProfileController {
    private final TripProfileService tripProfileService;

    @PostMapping("/create")
    public ApiResponse<TripProfileDTO> createTrip(@RequestBody TripProfileDTO dto) {
        TripProfileDTO result = tripProfileService.createTrip(dto);
        return ApiResponse.<TripProfileDTO>builder()
                .code(200)
                .message("Trip created successfully")
                .data(result)
                .build();
    }

    @GetMapping("/{tripId}")
    public ApiResponse<TripProfileDTO> getTrip(@PathVariable Long tripId) {
        TripProfileDTO result = tripProfileService.getTripWithReadinessChecks(tripId);
        return ApiResponse.<TripProfileDTO>builder()
                .code(200)
                .message("Trip retrieved successfully")
                .data(result)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<TripProfileDTO>> getUserTrips(@PathVariable String userId) {
        List<TripProfileDTO> result = tripProfileService.getUserTrips(userId);
        return ApiResponse.<List<TripProfileDTO>>builder()
                .code(200)
                .message("User trips retrieved successfully")
                .data(result)
                .build();
    }

    @PostMapping("/readiness/{checkId}/complete")
    public ApiResponse<String> completeReadinessCheck(@PathVariable Long checkId) {
        tripProfileService.completeReadinessCheck(checkId);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Readiness check completed")
                .data("OK")
                .build();
    }
}
