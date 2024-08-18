package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class TeamEliminator {
    final TeamEliminationStrategy strategy;

    public TeamEliminator(Config config) {
        boolean eliminateAfterRound = config.isEliminateAfterRound();
        boolean allPlayersRequired = config.isAllPlayersRequiredForTeamWin();
        if (!eliminateAfterRound) {
            strategy = new NoEliminationStrategy();
        } else if (allPlayersRequired) {
            strategy = new AllPlayersRequiredEliminationStrategy();
        } else {
            strategy = new AnyPlayerRequiredEliminationStrategy();
        }
    }

    public void eliminateTeams(Set<Team> teams) {
        strategy.eliminateTeams(teams);
    }
}
