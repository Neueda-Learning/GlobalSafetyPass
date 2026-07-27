package com.hsbc.travel.service;

import com.hsbc.travel.dto.ReadinessResult;
import com.hsbc.travel.entity.Card;
import com.hsbc.travel.entity.CardStatus;
import com.hsbc.travel.entity.Trip;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ReadinessService {

    public ReadinessResult evaluate(Trip trip, Card card) {
        ReadinessResult result = new ReadinessResult();
        int score = 100;

        if (trip.getEndDate().isBefore(trip.getStartDate())) {
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "INVALID_DATES", "End date cannot be before start date.", "ERROR"));
            result.setScore(0);
            result.getRecommendedActions().add("Please correct your travel dates.");
            return result;
        }

        if (trip.getStartDate().isBefore(LocalDate.now())) {
            score -= 10;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "PAST_START", "Trip start date is in the past.", "WARNING"));
            result.getRecommendedActions().add("Update start date if trip hasn't begun yet.");
        }

        if (card.getExpiryDate().isBefore(trip.getEndDate())) {
            score -= 30;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "CARD_EXPIRY", "Your card expires before the trip ends (" + card.getExpiryDate() + ").", "ERROR"));
            result.getRecommendedActions().add("Request a card renewal before travelling.");
        }

        if (card.getStatus() == CardStatus.EXPIRED) {
            score -= 40;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "CARD_EXPIRED", "Your card has already expired.", "ERROR"));
            result.getRecommendedActions().add("Replace your expired card immediately.");
        }

        if (card.getStatus() == CardStatus.FROZEN) {
            score -= 35;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "CARD_FROZEN", "Your card is currently frozen.", "ERROR"));
            result.getRecommendedActions().add("Unfreeze your card in card controls.");
        }

        if (!card.isOverseasEnabled()) {
            score -= 25;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "OVERSEAS_DISABLED", "Overseas payments are not enabled on this card.", "ERROR"));
            result.getRecommendedActions().add("Enable overseas payments before your trip.");
        }

        if (card.getBalance().compareTo(trip.getBudget()) < 0) {
            score -= 20;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "LOW_BALANCE", "Card balance (" + card.getBalance() + ") is less than trip budget (" + trip.getBudget() + ").", "WARNING"));
            result.getRecommendedActions().add("Top up your card or reduce your budget.");
        }

        if (card.getDailyLimit().compareTo(trip.getBudget().multiply(new BigDecimal("0.3"))) < 0) {
            score -= 15;
            result.getWarnings().add(new ReadinessResult.WarningItem(
                    "LOW_LIMIT", "Daily spending limit (" + card.getDailyLimit() + ") may be too low for this trip.", "WARNING"));
            result.getRecommendedActions().add("Consider increasing your daily spending limit.");
        }

        result.setScore(Math.max(0, score));
        if (result.getRecommendedActions().isEmpty()) {
            result.getRecommendedActions().add("You're all set! Have a great trip.");
        }
        return result;
    }
}
