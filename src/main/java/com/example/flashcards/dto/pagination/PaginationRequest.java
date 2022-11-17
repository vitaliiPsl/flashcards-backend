package com.example.flashcards.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationRequest {
    private int page;

    private int size;
}
