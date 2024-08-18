package me.stahu.gsblockshuffle.game.round;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class GameEndConditionChecker {

        private GameEndConditionChecker() {
        }

        public static boolean isGameEnd(Config config, Set<Team> teams, int round) {
            boolean endGameIfOneTeamRemaining = config.isEndGameIfOneTeamRemaining();
            int totalRounds = config.getTotalRounds();
            if (endGameIfOneTeamRemaining) {
                boolean isOneTeamRemaining = teams.stream()
                        .filter(team -> !team.isEliminated())
                        .count() == 1;
                if (isOneTeamRemaining) {
                    return true;
                }
            }
            if (totalRounds == -1) {
                return false;
            }
            return round >= totalRounds;
        }
}
