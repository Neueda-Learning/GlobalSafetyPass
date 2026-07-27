package com.globalsafetypass.service;

import com.globalsafetypass.dto.TripProfileDTO;
import com.globalsafetypass.dto.ReadinessCheckDTO;
import com.globalsafetypass.entity.TripProfile;
import com.globalsafetypass.entity.ReadinessCheck;
import com.globalsafetypass.repository.TripProfileRepository;
import com.globalsafetypass.repository.ReadinessCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripProfileService {
    private final TripProfileRepository tripProfileRepository;
    private final ReadinessCheckRepository readinessCheckRepository;

    public TripProfileDTO createTrip(TripProfileDTO dto) {
        TripProfile trip = TripProfile.builder()
                .userId(dto.getUserId())
                .destination(dto.getDestination())
                .departureDate(dto.getDepartureDate())
                .returnDate(dto.getReturnDate())
                .travelType(dto.getTravelType())
                .cardId(dto.getCardId())
                .readinessScore(0)
                .build();
        
        TripProfile saved = tripProfileRepository.save(trip);
        
        // 创建默认的准备检查项
        initializeReadinessChecks(saved.getId());
        
        return convertToDTO(saved);
    }

    private void initializeReadinessChecks(Long tripProfileId) {
        String[] checkTypes = {
            "card_valid", "card_expiry", "overseas_payment", 
            "limit", "app_notification", "backup_verification"
        };
        
        String[] descriptions = {
            "银行卡是否有效",
            "卡片是否会在旅途中到期",
            "海外支付是否开启",
            "消费和取现限额是否合适",
            "App通知和生物识别是否开启",
            "用户是否有备用验证方式"
        };
        
        for (int i = 0; i < checkTypes.length; i++) {
            ReadinessCheck check = ReadinessCheck.builder()
                    .tripProfileId(tripProfileId)
                    .checkType(checkTypes[i])
                    .isCompleted(false)
                    .description(descriptions[i])
                    .action("完成此项检查")
                    .build();
            readinessCheckRepository.save(check);
        }
    }

    public TripProfileDTO getTripProfile(Long tripId) {
        TripProfile trip = tripProfileRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return convertToDTO(trip);
    }

    public TripProfileDTO getTripWithReadinessChecks(Long tripId) {
        TripProfile trip = tripProfileRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        List<ReadinessCheck> checks = readinessCheckRepository.findByTripProfileId(tripId);
        TripProfileDTO dto = convertToDTO(trip);
        
        List<ReadinessCheckDTO> checkDTOs = checks.stream()
                .map(this::convertCheckToDTO)
                .collect(Collectors.toList());
        
        dto.setReadinessChecks(checkDTOs);
        
        // 计算readiness score
        long completedCount = checks.stream().filter(ReadinessCheck::getIsCompleted).count();
        dto.setReadinessScore((int) completedCount);
        
        return dto;
    }

    public List<TripProfileDTO> getUserTrips(String userId) {
        return tripProfileRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void completeReadinessCheck(Long checkId) {
        ReadinessCheck check = readinessCheckRepository.findById(checkId)
                .orElseThrow(() -> new RuntimeException("Check not found"));
        check.setIsCompleted(true);
        readinessCheckRepository.save(check);
    }

    private TripProfileDTO convertToDTO(TripProfile trip) {
        return TripProfileDTO.builder()
                .id(trip.getId())
                .userId(trip.getUserId())
                .destination(trip.getDestination())
                .departureDate(trip.getDepartureDate())
                .returnDate(trip.getReturnDate())
                .travelType(trip.getTravelType())
                .cardId(trip.getCardId())
                .readinessScore(trip.getReadinessScore())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }

    private ReadinessCheckDTO convertCheckToDTO(ReadinessCheck check) {
        return ReadinessCheckDTO.builder()
                .id(check.getId())
                .tripProfileId(check.getTripProfileId())
                .checkType(check.getCheckType())
                .isCompleted(check.getIsCompleted())
                .description(check.getDescription())
                .action(check.getAction())
                .build();
    }
}
