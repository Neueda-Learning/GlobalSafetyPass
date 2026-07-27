package com.globalsafetypass.repository;

import com.globalsafetypass.entity.TripProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripProfileRepository extends JpaRepository<TripProfile, Long> {
    List<TripProfile> findByUserId(String userId);
}
