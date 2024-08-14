package me.stahu.gsblockshuffle.game.difficulty;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

public class EveryNthRoundIncrementStrategy implements DifficultyIncrementStrategy {
    final int n;

    public EveryNthRoundIncrementStrategy(@Positive int n) {
        this.n = n;
    }

    @Override
    public int increaseDifficulty(int difficulty, @NonNegative int round) {
        if ((round + 1) % n == 0) {
            return difficulty + 1;
        } else {
            return difficulty;
        }
    }
}
