package me.stahu.gsblockshuffle.game.difficulty;

import me.stahu.gsblockshuffle.config.Config;

public class DifficultyIncrementer {
    final Config config;
    final DifficultyIncrementStrategy strategy;

    public DifficultyIncrementer(Config config) {
        this.config = config;
        if (!config.isIncreaseDifficulty()) {
            strategy = new NoIncrementStrategy();
            return;
        }
        if (config.getIncreaseEveryNRounds() == -1) {
            if (config.getCustomIncrease().isEmpty()) {
                throw new IllegalArgumentException("IncreaseEveryNRounds is -1 but the customIncrease list is empty");
            }
            strategy = new CustomIncrementStrategy(config.getCustomIncrease());
        } else {
            strategy = new EveryNthRoundIncrementStrategy(config.getIncreaseEveryNRounds());
        }
    }

    public int increaseDifficulty(int difficulty, int round) {
        return strategy.increaseDifficulty(difficulty, round);
    }
}
