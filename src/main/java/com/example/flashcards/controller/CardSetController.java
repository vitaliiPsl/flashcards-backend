package com.example.flashcards.controller;

import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.service.CardSetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sets")
public class CardSetController {
    private final CardSetService cardSetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardSetDto saveSet(@RequestBody @Valid CardSetDto cardSetDto, Authentication authentication) {
        return cardSetService.saveSet(cardSetDto, authentication);
    }

    @PutMapping("{id}")
    public CardSetDto replaceSet(@PathVariable long id, @RequestBody @Valid CardSetDto cardSetDto, Authentication authentication) {
        return cardSetService.replaceSet(id, cardSetDto, authentication);
    }

    @DeleteMapping("{id}")
    public void deleteSet(@PathVariable long id, Authentication authentication) {
        cardSetService.deleteSet(id, authentication);
    }

    @GetMapping("{id}")
    public CardSetDto getSet(@PathVariable long id, Authentication authentication) {
        return cardSetService.getSetById(id, authentication);
    }
}
