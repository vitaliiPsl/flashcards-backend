package com.example.flashcards.controller;

import com.example.flashcards.api.SetApi;
import com.example.flashcards.dto.pagination.PaginationRequest;
import com.example.flashcards.dto.pagination.PaginationResponse;
import com.example.flashcards.dto.set.CardSetDto;
import com.example.flashcards.service.CardSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

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

    public PaginationResponse<CardSetDto> getSets(Long authorId, String name, PaginationRequest pagination, Authentication auth) {
        if (authorId != null && name != null) {
            return cardSetService.getSetsByAuthorAndName(authorId, name, pagination, auth);
        } else if (authorId != null) {
            return cardSetService.getSetsByAuthor(authorId, pagination, auth);
        } else if (name != null) {
            return cardSetService.getPublicSetsByName(name, pagination);
        }

        return cardSetService.getPublicSets(pagination);
    }
}
