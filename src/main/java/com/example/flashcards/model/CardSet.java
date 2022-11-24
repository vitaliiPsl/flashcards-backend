package com.example.flashcards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flash_card_set", uniqueConstraints = {
        @UniqueConstraint(name = "uq_card_set_and_author", columnNames = {"name", "author_id"})
})
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    private String name;
    private String description;

    private SetType type = SetType.PUBLIC;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
        return Objects.equals(id, cardSet.id) && Objects.equals(author, cardSet.author) && Objects.equals(name, cardSet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}
