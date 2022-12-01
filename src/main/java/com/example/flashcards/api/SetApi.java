package com.example.flashcards.api;

import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.dto.pagination.PaginationResponse;
import com.example.flashcards.dto.set.CardSetDto;
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

@Tag(name = "Sets API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sets")
public interface SetApi {

    @Operation(summary = "Create new set")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CardSetDto saveSet(@RequestBody @Valid CardSetDto cardSetDto, Authentication auth);

    @Operation(summary = "Replace set")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Set doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
    })
    @PutMapping("{id}")
    CardSetDto replaceSet(@PathVariable long id, @RequestBody @Valid CardSetDto cardSetDto, Authentication auth);

    @Operation(summary = "Delete set")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Set doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
    })
    @DeleteMapping("{id}")
    void deleteSet(@PathVariable long id, Authentication auth);

    @Operation(summary = "Get set")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Set doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
    })
    @GetMapping("{id}")
    CardSetDto getSet(@PathVariable long id, Authentication auth);

    @Operation(summary = "Get all sets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping
    PaginationResponse<CardSetDto> getSets(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String name,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Authentication auth
    );
}
