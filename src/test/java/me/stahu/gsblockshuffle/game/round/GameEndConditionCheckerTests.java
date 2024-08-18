package me.stahu.gsblockshuffle.game.round;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

class GameEndConditionCheckerTests {

    private Config config;
    private Set<Team> teams;

    @BeforeEach
    void setUp() {
        config = Mockito.mock(Config.class);
        Mockito.when(config.getTotalRounds()).thenReturn(5);
        teams = new HashSet<>();
    }

    @Test
    void isGameEnd_whenOneTeamRemainingAndConfigAllowsEndGame_shouldReturnTrue() {
        Mockito.when(config.isEndGameIfOneTeamRemaining()).thenReturn(true);
        Team team1 = Mockito.mock(Team.class);
        Team team2 = Mockito.mock(Team.class);
        Mockito.when(team1.isEliminated()).thenReturn(false);
        Mockito.when(team2.isEliminated()).thenReturn(true);
        teams.add(team1);
        teams.add(team2);

        assertTrue(GameEndConditionChecker.isGameEnd(config, teams, 0));
    }

    @Test
    void isGameEnd_whenMultipleTeamsRemainingAndConfigAllowsEndGame_shouldReturnFalse() {
        Mockito.when(config.isEndGameIfOneTeamRemaining()).thenReturn(true);
        Team team1 = Mockito.mock(Team.class);
        Team team2 = Mockito.mock(Team.class);
        Mockito.when(team1.isEliminated()).thenReturn(false);
        Mockito.when(team2.isEliminated()).thenReturn(false);
        teams.add(team1);
        teams.add(team2);

        assertFalse(GameEndConditionChecker.isGameEnd(config, teams, 0));
    }

    @Test
    void isGameEnd_whenTotalRoundsExceeded_shouldReturnTrue() {
        Mockito.when(config.getTotalRounds()).thenReturn(5);

        assertTrue(GameEndConditionChecker.isGameEnd(config, teams, 6));
    }

    @Test
    void isGameEnd_whenTotalRoundsNotExceeded_shouldReturnFalse() {
        assertFalse(GameEndConditionChecker.isGameEnd(config, teams, 4));
    }

    @Test
    void isGameEnd_whenTotalRoundsUnlimited_shouldReturnFalse() {
        Mockito.when(config.getTotalRounds()).thenReturn(-1);

        assertFalse(GameEndConditionChecker.isGameEnd(config, teams, 100));
    }
}
