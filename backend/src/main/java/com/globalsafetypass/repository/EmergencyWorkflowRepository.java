package com.globalsafetypass.repository;

import com.globalsafetypass.entity.EmergencyWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyWorkflowRepository extends JpaRepository<EmergencyWorkflow, Long> {
    List<EmergencyWorkflow> findByTripProfileId(Long tripProfileId);
}
