package com.example.flashcards.controller;

import com.example.flashcards.api.CardApi;
import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.requests.PaginationRequest;
import com.example.flashcards.dto.responses.PaginationResponse;
import com.example.flashcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CardController implements CardApi {
    private final CardService cardService;

    public CardDto saveCard(long setId, CardDto cardDto, Authentication auth) {
        return cardService.saveCard(setId, cardDto, auth);
    }

    public void deleteCard(long cardId, long setId, Authentication auth) {
        cardService.deleteCard(cardId, setId, auth);
    }

    public CardDto replaceCard(long cardId, long setId, CardDto cardDto, Authentication auth) {
        return cardService.replaceCard(cardId, setId, cardDto, auth);
    }

    public CardDto getCard(long cardId, long setId, Authentication auth) {
        return cardService.getCardById(cardId, setId, auth);
    }

    public PaginationResponse<CardDto> getCards(long setId, PaginationRequest pagination, Authentication auth) {
        return cardService.getCards(setId, auth, pagination);
    }
}
