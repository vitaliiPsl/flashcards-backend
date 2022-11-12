package com.example.flashcards.api;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.requests.PaginationRequest;
import com.example.flashcards.dto.responses.PaginationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sets/{setId}/cards")
public interface CardApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CardDto saveCard(@PathVariable long setId, @RequestBody @Valid CardDto cardDto, Authentication auth);

    @DeleteMapping("{cardId}")
    void deleteCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth);

    @PutMapping("{cardId}")
    CardDto replaceCard(
            @PathVariable long cardId,
            @PathVariable long setId,
            @RequestBody @Valid CardDto cardDto,
            Authentication auth
    );

    @GetMapping("{cardId}")
    CardDto getCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth);

    @GetMapping
    PaginationResponse<CardDto> getCards(
            @PathVariable long setId,
            @Valid PaginationRequest pagination,
            Authentication auth
    );
}
