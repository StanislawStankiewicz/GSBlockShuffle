package me.stahu.gsblockshuffle.game.difficulty;

import java.util.List;

public class CustomIncrementStrategy implements DifficultyIncrementStrategy {
    final List<Integer> difficulties;

    public CustomIncrementStrategy(List<Integer> difficulties) {
        this.difficulties = difficulties;
    }

    @Override
    public int increaseDifficulty(int difficulty, int round) {
        return difficulties.get(Math.min(round, difficulties.size() - 1));
    }
}
