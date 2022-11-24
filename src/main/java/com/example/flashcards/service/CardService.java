package com.example.flashcards.service;

import com.example.flashcards.dto.card.CardDto;
import org.springframework.security.core.Authentication;

/**
 * Flash cards business logic
 */
public interface CardService {
    /**
     * Save a new card. Verify that the user is the author of the set
     * and that set doesn't already contain the card with provided front value
     *
     * @param setId   id of the set
     * @param cardDto card to save
     * @param auth    currently authenticated user
     * @return saved card
     */
    CardDto saveCard(long setId, CardDto cardDto, Authentication auth);

    /**
     * Save a card with given id. Verify that the user is the author of the set
     * and that the card with given belongs to the set
     *
     * @param cardId id of the card to delete
     * @param setId  id of the set card belongs to
     * @param auth   currently authenticated user
     */
    void deleteCard(long cardId, long setId, Authentication auth);

    /**
     * Update existing card. Verify that the user is the author of the set
     * and that the card with given belongs to the set
     *
     * @param cardId  id of the card to update
     * @param setId   id of the set card belongs to
     * @param cardDto new card data
     * @param auth    authenticated user
     * @return updated card
     */
    CardDto replaceCard(long cardId, long setId, CardDto cardDto, Authentication auth);

    /**
     * Retrieves the card by id if it exists, belongs to the set
     * and if it is public or the user is the author of the set
     *
     * @param cardId id of the card
     * @param setId  id of the set card should belong to
     * @param auth   authenticated user
     * @return retrieved card
     */
    CardDto getCardById(long cardId, long setId, Authentication auth);
}
