package com.example.flashcards.dto.responses;

import com.example.flashcards.dto.requests.PaginationRequest;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginationResponse<T> {
    private List<T> content;

    private long totalElements;
    private int totalPages;
    private boolean last;

    private int page;
    private int size;

    public PaginationResponse(Page<T> page, PaginationRequest paginationRequest) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.page = paginationRequest.getPage();
        this.size = page.getSize();
    }
}
