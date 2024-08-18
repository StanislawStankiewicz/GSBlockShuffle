package me.stahu.gsblockshuffle.game.difficulty;

public class NoIncrementStrategy implements DifficultyIncrementStrategy {

    @Override
    public int increaseDifficulty(int difficulty, int round) {
        return difficulty;
    }
}
