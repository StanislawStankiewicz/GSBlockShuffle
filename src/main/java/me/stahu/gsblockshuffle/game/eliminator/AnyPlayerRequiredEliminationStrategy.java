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
                if (player.hasFoundBlock()) {
                    hasFoundBlock = true;
                    break;
                }
            }
            System.out.println("Team " + team + " has found block: " + hasFoundBlock);
            if (!hasFoundBlock) {
                System.out.println("Team " + team + " is eliminated");
                team.setEliminated(true);
            }
            System.out.println("Team " + team + " is eliminated: " + team.isEliminated());
        }
    }
}
