package com.globalsafetypass.repository;

import com.globalsafetypass.entity.ReadinessCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadinessCheckRepository extends JpaRepository<ReadinessCheck, Long> {
    List<ReadinessCheck> findByTripProfileId(Long tripProfileId);
}
