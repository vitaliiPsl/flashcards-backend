package com.example.flashcards.api;

import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.dto.errors.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Cards API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sets/{setId}/cards")
public interface CardApi {

    @Operation(summary = "Save new card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Set doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CardDto saveCard(@PathVariable long setId, @RequestBody @Valid CardDto cardDto, Authentication auth);

    @Operation(summary = "Delete card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Set or card doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("{cardId}")
    void deleteCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth);

    @Operation(summary = "Replace card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Set or card doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PutMapping("{cardId}")
    CardDto replaceCard(
            @PathVariable long cardId,
            @PathVariable long setId,
            @RequestBody @Valid CardDto cardDto,
            Authentication auth
    );

    @Operation(summary = "Get card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Set or card doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("{cardId}")
    CardDto getCard(@PathVariable long cardId, @PathVariable long setId, Authentication auth);
}
