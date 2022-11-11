package com.example.flashcards.model.learning;

import com.example.flashcards.model.Card;
import com.example.flashcards.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Card card;

    @ManyToOne
    private User user;

    private CardSide cardSide;

    private String question;
    private String correctAnswer;

    @ElementCollection
    private List<String> options;

    private String answer;
    private boolean correct;
    private LocalDateTime answeredAt;

    public boolean isClosed() {
        return answer != null;
    }
}
