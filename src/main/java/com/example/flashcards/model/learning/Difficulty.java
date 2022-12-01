package com.example.flashcards.model.learning;

public enum Difficulty {
    HARD(0.5), GOOD(0.8), EASY(1);

    private final double distribution;

    Difficulty(double distribution) {
        this.distribution = distribution;
    }

    public double getDistribution() {
        return distribution;
    }
}
