package com.hsbc.travel.service;

import com.hsbc.travel.dto.CardActionRequest;
import com.hsbc.travel.entity.Card;
import com.hsbc.travel.entity.CardStatus;
import com.hsbc.travel.exception.ResourceNotFoundException;
import com.hsbc.travel.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));
    }

    @Transactional
    public Card updateCardControls(Long cardId, CardActionRequest request) {
        Card card = getCard(cardId);
        if (request.isEnableOverseas()) {
            card.setOverseasEnabled(true);
        }
        if (request.getNewDailyLimit() != null) {
            card.setDailyLimit(request.getNewDailyLimit());
        }
        return cardRepository.save(card);
    }

    @Transactional
    public Card freezeCard(Long cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.FROZEN);
        return cardRepository.save(card);
    }

    @Transactional
    public Card unfreezeCard(Long cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }
}
