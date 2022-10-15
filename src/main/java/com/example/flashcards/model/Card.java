package com.example.flashcards.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flash_card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String front;
    private String back;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
