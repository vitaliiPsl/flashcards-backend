package com.example.flashcards.controller;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sets/{setId}/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto saveCard(@PathVariable long setId, @RequestBody @Valid CardDto cardDto, Authentication auth) {
        return cardService.saveCard(setId, cardDto, auth);
    }

    @DeleteMapping("{cardId}")
    public void deleteCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth) {
        cardService.deleteCard(cardId, setId, auth);
    }

    @PutMapping("{cardId}")
    public CardDto replaceCard(
            @PathVariable long cardId,
            @PathVariable long setId,
            @RequestBody @Valid CardDto cardDto,
            Authentication auth
    ) {
        return cardService.replaceCard(cardId, setId, cardDto, auth);
    }

    @GetMapping("{cardId}")
    public CardDto getCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth) {
        return cardService.getCardById(cardId, setId, auth);
    }
}
