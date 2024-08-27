package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class AllPlayersRequiredEliminationStrategy implements TeamEliminationStrategy {
    @Override
    public void eliminateTeams(Set<Team> teams) {
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                if (!player.isFoundBlock()) {
                    team.setEliminated(true);
                    break;
                }
            }
        }
    }
}
