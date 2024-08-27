package me.stahu.gsblockshuffle.game.round;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.util.Set;

class RoundEndConditionCheckerTests {

    @ParameterizedTest
    @CsvSource({
            "true, false, true, true, true",
            "false, true, true, false, true",
            "false, false, false, false, false",
            "true, true, true, false, false"
    })
    void isRoundEndConditionMet(boolean allPlayersRequired, boolean firstToWin, boolean player1Found, boolean player2Found, boolean expected) {
        Config config = mock(Config.class);
        when(config.isAllPlayersRequiredForTeamWin()).thenReturn(allPlayersRequired);
        when(config.isFirstToWin()).thenReturn(firstToWin);

        Player player1 = mock(Player.class);
        when(player1.isFoundBlock()).thenReturn(player1Found);
        Player player2 = mock(Player.class);
        when(player2.isFoundBlock()).thenReturn(player2Found);

        Team team = mock(Team.class);
        when(team.getPlayers()).thenReturn(Set.of(player1, player2));

        if (expected) {
            assertTrue(RoundEndConditionChecker.isRoundEndConditionMet(config, Set.of(team)));
        } else {
            assertFalse(RoundEndConditionChecker.isRoundEndConditionMet(config, Set.of(team)));
        }
    }

    @Test
    void isRoundEndConditionMet_AllPlayersFoundBlock() {
        Config config = mock(Config.class);
        when(config.isAllPlayersRequiredForTeamWin()).thenReturn(false);
        when(config.isFirstToWin()).thenReturn(false);

        Player player1 = mock(Player.class);
        when(player1.isFoundBlock()).thenReturn(true);
        Player player2 = mock(Player.class);
        when(player2.isFoundBlock()).thenReturn(true);

        Team team = mock(Team.class);
        when(team.getPlayers()).thenReturn(Set.of(player1, player2));

        assertTrue(RoundEndConditionChecker.isRoundEndConditionMet(config, Set.of(team)));
    }

    @Test
    void isRoundEndConditionMet_AnyPlayerFoundBlock() {
        Config config = mock(Config.class);
        when(config.isAllPlayersRequiredForTeamWin()).thenReturn(false);
        when(config.isFirstToWin()).thenReturn(true);

        Player player1 = mock(Player.class);
        when(player1.isFoundBlock()).thenReturn(true);
        Player player2 = mock(Player.class);
        when(player2.isFoundBlock()).thenReturn(false);

        Team team = mock(Team.class);
        when(team.getPlayers()).thenReturn(Set.of(player1, player2));

        assertTrue(RoundEndConditionChecker.isRoundEndConditionMet(config, Set.of(team)));
    }

    @Test
    void isRoundEndConditionMet_AllTeamsFoundBlock() {
        Config config = mock(Config.class);
        when(config.isAllPlayersRequiredForTeamWin()).thenReturn(true);
        when(config.isFirstToWin()).thenReturn(false);

        Player player1 = mock(Player.class);
        when(player1.isFoundBlock()).thenReturn(true);
        Player player2 = mock(Player.class);
        when(player2.isFoundBlock()).thenReturn(true);

        Team team1 = mock(Team.class);
        when(team1.getPlayers()).thenReturn(Set.of(player1, player2));

        Player player3 = mock(Player.class);
        when(player3.isFoundBlock()).thenReturn(true);
        Player player4 = mock(Player.class);
        when(player4.isFoundBlock()).thenReturn(true);

        Team team2 = mock(Team.class);
        when(team2.getPlayers()).thenReturn(Set.of(player3, player4));

        assertTrue(RoundEndConditionChecker.isRoundEndConditionMet(config, Set.of(team1, team2)));
    }
}