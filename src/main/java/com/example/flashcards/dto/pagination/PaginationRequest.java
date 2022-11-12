package com.example.flashcards.dto.pagination;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PaginationRequest {
    @Min(value = 0, message = "Page index cannot be less than 0")
    private int page = 0;

    @Min(value = 1, message = "Page size cannot be less than 1")
    private int size = 10;
}
