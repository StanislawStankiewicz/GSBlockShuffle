package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.model.Team;

public interface ScoreIncrementStrategy {
    int awardPoints(Team team);
}
