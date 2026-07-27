package com.globalsafetypass.repository;

import com.globalsafetypass.entity.FraudReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudReportRepository extends JpaRepository<FraudReport, Long> {
    List<FraudReport> findByTripProfileId(Long tripProfileId);
}
