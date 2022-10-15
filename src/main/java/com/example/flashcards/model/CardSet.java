package com.example.flashcards.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "flash_card_set")
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private SetType type = SetType.PUBLIC;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    private Set<Card> cards = new HashSet<>();

    public boolean isPrivate() {
        return type == SetType.PRIVATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSet cardSet = (CardSet) o;
        return Objects.equals(id, cardSet.id) && Objects.equals(name, cardSet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
