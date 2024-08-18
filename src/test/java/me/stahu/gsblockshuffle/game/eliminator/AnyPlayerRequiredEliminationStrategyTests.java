package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.stahu.gsblockshuffle.game.eliminator.EliminatorTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnyPlayerRequiredEliminationStrategyTests {
    @Test
    void eliminateTeams_EliminatesTheRightTeams() {
        AnyPlayerRequiredEliminationStrategy strategy = new AnyPlayerRequiredEliminationStrategy();
        Set<Team> teams = arrangeTeams(3, 3);

        List<Team> teamsList = new ArrayList<>(teams);
        // First team passes
        for (Player player : teamsList.get(0).getPlayers()) {
            player.setFoundBlock(true);
        }
        // Second team fails
        for (Player player : teamsList.get(1).getPlayers()) {
            player.setFoundBlock(false);
        }
        // Third team passes
        teamsList.get(2).getPlayers().iterator().next().setFoundBlock(true);

        strategy.eliminateTeams(teams);

        int eliminatedCount = 0;
        for (Team team : teams) {
            if (team.isEliminated()) {
                eliminatedCount++;
            }
        }

        assertEquals(1, eliminatedCount);
    }
}
