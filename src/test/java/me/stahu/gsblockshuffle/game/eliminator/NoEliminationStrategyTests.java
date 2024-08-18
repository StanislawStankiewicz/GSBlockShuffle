package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static me.stahu.gsblockshuffle.game.eliminator.EliminatorTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.*;

public class NoEliminationStrategyTests {

    @Test
    void eliminateTeams_DoesNotEliminateAnyTeams() {
        NoEliminationStrategy strategy = new NoEliminationStrategy();
        Set<Team> teams = arrangeTeams(3, 3);

        strategy.eliminateTeams(teams);

        for (Team team : teams) {
            assertFalse(team.isEliminated());
        }
    }
}