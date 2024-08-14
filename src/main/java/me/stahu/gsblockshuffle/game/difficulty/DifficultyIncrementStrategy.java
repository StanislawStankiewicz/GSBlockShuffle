package me.stahu.gsblockshuffle.game.difficulty;

public interface DifficultyIncrementStrategy {

    int increaseDifficulty(int difficulty, int round);
}
