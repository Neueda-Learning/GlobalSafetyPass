package com.globalsafetypass.repository;

import com.globalsafetypass.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findByTripProfileId(Long tripProfileId);
    List<TransactionRecord> findByCardId(String cardId);
}
