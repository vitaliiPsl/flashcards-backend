package com.example.flashcards.api;

import com.example.flashcards.dto.pagination.PaginationRequest;
import com.example.flashcards.dto.pagination.PaginationResponse;
import com.example.flashcards.dto.set.CardSetDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sets")
public interface SetApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CardSetDto saveSet(@RequestBody @Valid CardSetDto cardSetDto, Authentication auth);

    @PutMapping("{id}")
    CardSetDto replaceSet(@PathVariable long id, @RequestBody @Valid CardSetDto cardSetDto, Authentication auth);

    @DeleteMapping("{id}")
    void deleteSet(@PathVariable long id, Authentication auth);

    @GetMapping("{id}")
    CardSetDto getSet(@PathVariable long id, Authentication auth);

    @GetMapping
    PaginationResponse<CardSetDto> getSets(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String name,
            PaginationRequest pagination,
            Authentication auth
    );
}
