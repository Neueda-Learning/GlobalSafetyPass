package com.hsbc.travel.repository;

import com.hsbc.travel.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
