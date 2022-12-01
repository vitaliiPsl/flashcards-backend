package com.example.flashcards.api;

import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.dto.learning.QuestionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Learning API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/learning")
public interface LearningApi {

    @Operation(summary = "Create question")
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
    @PostMapping(path = "questions", params = "setId")
    QuestionDto createQuestion(
            @RequestParam long setId,
            @RequestBody QuestionDto questionDto,
            Authentication auth
    );

    @Operation(summary = "Submit answer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Question doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PutMapping("questions/{questionId}")
    QuestionDto submitAnswer(
            @PathVariable long questionId,
            @RequestBody QuestionAnswerDto answer,
            Authentication auth
    );
}
