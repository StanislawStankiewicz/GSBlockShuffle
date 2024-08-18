package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

public class PerRoundAwardStrategy implements ScoreIncrementStrategy {

    @Override
    public int awardPoints(Team team) {
        if (team.getPlayers().stream()
                .allMatch(Player::hasFoundBlock)) {
            return 1;
        }
        return 0;
    }
}
