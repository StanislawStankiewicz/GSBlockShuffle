package me.stahu.gsblockshuffle.game.difficulty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EveryNthRoundIncrementStrategyTests {

    @Test
    void increaseDifficulty_IncrementsDifficultyOnNthRound() {
        EveryNthRoundIncrementStrategy strategy = new EveryNthRoundIncrementStrategy(3);

        assertEquals(1, strategy.increaseDifficulty(0, 3));
        assertEquals(2, strategy.increaseDifficulty(1, 6));
    }

    @Test
    void increaseDifficulty_DoesNotIncrementDifficultyOnNonNthRound() {
        EveryNthRoundIncrementStrategy strategy = new EveryNthRoundIncrementStrategy(3);

        assertEquals(0, strategy.increaseDifficulty(0, 1));
        assertEquals(1, strategy.increaseDifficulty(1, 4));
    }
}