package com.example.flashcards.controller;

import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.service.CardSetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sets")
public class CardSetController {
    private final CardSetService cardSetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardSetDto saveSet(@RequestBody @Valid CardSetDto cardSetDto, Authentication auth) {
        return cardSetService.saveSet(cardSetDto, auth);
    }

    @PutMapping("{id}")
    public CardSetDto replaceSet(@PathVariable long id, @RequestBody @Valid CardSetDto cardSetDto, Authentication auth) {
        return cardSetService.replaceSet(id, cardSetDto, auth);
    }

    @DeleteMapping("{id}")
    public void deleteSet(@PathVariable long id, Authentication auth) {
        cardSetService.deleteSet(id, auth);
    }

    @GetMapping("{id}")
    public CardSetDto getSet(@PathVariable long id, Authentication auth) {
        return cardSetService.getSetById(id, auth);
    }

    @GetMapping(params = "authorId")
    public List<CardSetDto> getSets(@RequestParam(required = false) long authorId, Authentication auth) {
        return cardSetService.getSetsByAuthor(authorId, auth);
    }
}
