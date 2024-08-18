package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.model.Team;

public class PerPlayerAwardStrategy implements ScoreIncrementStrategy {

    @Override
    public int awardPoints(Team team) {
        return 1;
    }

}
