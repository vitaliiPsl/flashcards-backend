package com.example.flashcards.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "flash_card", uniqueConstraints = {
        @UniqueConstraint(name = "uq_front_and_set", columnNames = {"front", "set_id"})
})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String front;
    private String back;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    private CardSet set;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(front, card.front) && Objects.equals(set, card.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(front, set);
    }
}
