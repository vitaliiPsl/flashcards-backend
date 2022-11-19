package com.example.flashcards.repository;

import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findBySet(CardSet set, Pageable pageable);
}
