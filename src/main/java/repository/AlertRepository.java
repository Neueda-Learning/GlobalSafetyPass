package com.hsbc.travel.repository;

import com.hsbc.travel.entity.Alert;
import com.hsbc.travel.entity.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByTripIdOrderByCreatedAtDesc(Long tripId);
    List<Alert> findByStatus(AlertStatus status);
}
