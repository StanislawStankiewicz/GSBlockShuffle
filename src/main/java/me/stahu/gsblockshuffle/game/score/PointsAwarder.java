package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Team;

public class PointsAwarder {
    final ScoreIncrementStrategy strategy;

    public PointsAwarder(Config config) {
        if (config.isTeamScoreIncrementPerPlayer()) {
            strategy = new PerPlayerAwardStrategy();
        } else {
            strategy = new PerRoundAwardStrategy();
        }
    }

    public int awardPoints(Team team) {
        return strategy.awardPoints(team);
    }
}
