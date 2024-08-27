package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class AnyPlayerRequiredEliminationStrategy implements TeamEliminationStrategy {
    @Override
    public void eliminateTeams(Set<Team> teams) {
        for (Team team : teams) {
            boolean hasFoundBlock = false;
            for (Player player : team.getPlayers()) {
                if (player.isFoundBlock()) {
                    hasFoundBlock = true;
                    break;
                }
            }
            if (!hasFoundBlock) {
                team.setEliminated(true);
            }
        }
    }
}
