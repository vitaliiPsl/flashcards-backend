package com.example.flashcards.controller;

import com.example.flashcards.api.SetApi;
import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.service.CardSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CardSetController implements SetApi {
    private final CardSetService cardSetService;

    public CardSetDto saveSet(CardSetDto cardSetDto, Authentication auth) {
        return cardSetService.saveSet(cardSetDto, auth);
    }

    public CardSetDto replaceSet(long id, CardSetDto cardSetDto, Authentication auth) {
        return cardSetService.replaceSet(id, cardSetDto, auth);
    }

    public void deleteSet(long id, Authentication auth) {
        cardSetService.deleteSet(id, auth);
    }

    public CardSetDto getSet(long id, Authentication auth) {
        return cardSetService.getSetById(id, auth);
    }

    public List<CardSetDto> getSets(long authorId, Authentication auth) {
        return cardSetService.getSetsByAuthor(authorId, auth);
    }
}
