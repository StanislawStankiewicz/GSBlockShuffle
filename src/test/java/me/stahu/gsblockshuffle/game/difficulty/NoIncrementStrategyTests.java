package me.stahu.gsblockshuffle.game.difficulty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoIncrementStrategyTests {

    @Test
    void increaseDifficulty_ReturnsSameDifficultyRegardlessOfRound() {
        NoIncrementStrategy strategy = new NoIncrementStrategy();

        assertEquals(5, strategy.increaseDifficulty(5, 0));
        assertEquals(5, strategy.increaseDifficulty(5, 1));
        assertEquals(5, strategy.increaseDifficulty(5, 10));
    }

    @Test
    void increaseDifficulty_ReturnsSameDifficultyForNegativeRounds() {
        NoIncrementStrategy strategy = new NoIncrementStrategy();

        assertEquals(5, strategy.increaseDifficulty(5, -1));
        assertEquals(5, strategy.increaseDifficulty(5, -10));
    }

    @Test
    void increaseDifficulty_ReturnsSameDifficultyForZeroDifficulty() {
        NoIncrementStrategy strategy = new NoIncrementStrategy();

        assertEquals(0, strategy.increaseDifficulty(0, 0));
        assertEquals(0, strategy.increaseDifficulty(0, 1));
        assertEquals(0, strategy.increaseDifficulty(0, 10));
    }
}