package me.stahu.gsblockshuffle.game.difficulty;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomIncrementStrategyTests {

    @Test
    void increaseDifficulty_ReturnsCorrectDifficultyForGivenRound() {
        List<Integer> difficulties = List.of(1, 2, 3);
        CustomIncrementStrategy strategy = new CustomIncrementStrategy(difficulties);

        assertEquals(1, strategy.increaseDifficulty(0, 1));
        assertEquals(2, strategy.increaseDifficulty(0, 2));
        assertEquals(3, strategy.increaseDifficulty(0, 3));
    }

    @Test
    void increaseDifficulty_ReturnsLastDifficultyWhenRoundExceedsListSize() {
        List<Integer> difficulties = List.of(1, 2, 3);
        CustomIncrementStrategy strategy = new CustomIncrementStrategy(difficulties);

        assertEquals(3, strategy.increaseDifficulty(0, 3));
        assertEquals(3, strategy.increaseDifficulty(0, 4));
    }

    @Test
    void increaseDifficulty_ThrowsExceptionWhenDifficultiesListIsEmpty() {
        List<Integer> difficulties = List.of();
        CustomIncrementStrategy strategy = new CustomIncrementStrategy(difficulties);

        assertThrows(IndexOutOfBoundsException.class, () -> strategy.increaseDifficulty(0, 0));
    }
}