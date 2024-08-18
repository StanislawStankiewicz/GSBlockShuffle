package me.stahu.gsblockshuffle.game.difficulty;

import me.stahu.gsblockshuffle.config.Config;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DifficultyIncrementerTests {

    @Test
    void increaseDifficulty_UsesNoIncrementStrategyWhenIncreaseDifficultyIsFalse() {
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.isIncreaseDifficulty()).thenReturn(false);

        DifficultyIncrementer incrementer = new DifficultyIncrementer(config);
        int newDifficulty = incrementer.increaseDifficulty(1, 1);

        assertEquals(1, newDifficulty);
    }

    @Test
    void increaseDifficulty_UsesCustomIncrementStrategyWhenIncreaseEveryNRoundsIsMinusOne() {
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.isIncreaseDifficulty()).thenReturn(true);
        Mockito.when(config.getIncreaseEveryNRounds()).thenReturn(-1);
        Mockito.when(config.getCustomIncrease()).thenReturn(List.of(1, 2, 3));

        DifficultyIncrementer incrementer = new DifficultyIncrementer(config);

        assertInstanceOf(CustomIncrementStrategy.class, incrementer.strategy);
    }

    @Test
    void increaseDifficulty_UsesEveryNthRoundIncrementStrategyWhenIncreaseEveryNRoundsIsPositive() {
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.isIncreaseDifficulty()).thenReturn(true);
        Mockito.when(config.getIncreaseEveryNRounds()).thenReturn(3);

        DifficultyIncrementer incrementer = new DifficultyIncrementer(config);

        assertInstanceOf(EveryNthRoundIncrementStrategy.class, incrementer.strategy);
    }

    @Test
    void increaseDifficulty_ThrowsExceptionWhenCustomIncreaseListIsEmpty() {
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.isIncreaseDifficulty()).thenReturn(true);
        Mockito.when(config.getIncreaseEveryNRounds()).thenReturn(-1);
        Mockito.when(config.getCustomIncrease()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> new DifficultyIncrementer(config));
    }
}