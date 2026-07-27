package com.hsbc.travel.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FailureCodeMapper {

    private static final Map<String, String> MESSAGES = Map.of(
            "INSUFFICIENT_BALANCE", "Your card doesn't have enough funds to complete this payment.",
            "CARD_FROZEN", "Your card is currently frozen and cannot be used for payments.",
            "LIMIT_EXCEEDED", "This transaction exceeds your daily spending limit.",
            "OVERSEAS_DISABLED", "Overseas payments are not enabled on this card.",
            "CARD_EXPIRED", "Your card has expired and cannot be used."
    );

    private static final Map<String, String> ACTIONS = Map.of(
            "INSUFFICIENT_BALANCE", "Top up your card balance or use a different payment method.",
            "CARD_FROZEN", "Unfreeze your card in Card Controls to resume payments.",
            "LIMIT_EXCEEDED", "Increase your daily limit or try a smaller amount.",
            "OVERSEAS_DISABLED", "Enable overseas payments in Card Controls.",
            "CARD_EXPIRED", "Request a new card from your bank."
    );

    public String getMessage(String code) {
        return MESSAGES.getOrDefault(code, "Payment could not be processed. Please try again.");
    }

    public String getRecommendedAction(String code) {
        return ACTIONS.getOrDefault(code, "Contact customer support for assistance.");
    }
}
