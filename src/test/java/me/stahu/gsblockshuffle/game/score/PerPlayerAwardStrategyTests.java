package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerPlayerAwardStrategyTests {

    private Config config;

    @BeforeEach
    void setUp() {
        config = mock(Config.class);
    }

    @Test
    void constructorAssignsPerPlayerStrategyWhenConfigIsSet() {
        when(config.isTeamScoreIncrementPerPlayer()).thenReturn(true);

        PointsAwarder pointsAwarder = new PointsAwarder(config);

        assertInstanceOf(PerPlayerAwardStrategy.class, pointsAwarder.strategy);
    }

    @Test
    void constructorAssignsPerRoundStrategyWhenConfigIsNotSet() {
        when(config.isTeamScoreIncrementPerPlayer()).thenReturn(false);

        PointsAwarder pointsAwarder = new PointsAwarder(config);

        assertInstanceOf(PerRoundAwardStrategy.class, pointsAwarder.strategy);
    }

    @Test
    void awardPoints_ReturnsCorrectPointsForPerPlayerStrategy() {
        when(config.isTeamScoreIncrementPerPlayer()).thenReturn(true);
        Team team = mock(Team.class);
        PointsAwarder awarder = new PointsAwarder(config);
        assertEquals(1, awarder.awardPoints(team));
    }

    @Test
    void awardPoints_ReturnsCorrectPointsForPerRoundStrategy() {
        when(config.isTeamScoreIncrementPerPlayer()).thenReturn(false);
        Team team = mock(Team.class);
        PointsAwarder awarder = new PointsAwarder(config);
        assertEquals(1, awarder.awardPoints(team));
    }

    @Test
    void constructor_ThrowsExceptionForNullConfig() {
        // noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> new PointsAwarder(null));
    }

    @Test
    void awardPoints_ReturnsOneWhenAllPlayersFoundBlock() {
        Team team = mock(Team.class);
        when(team.getPlayers()).thenReturn(Set.of(
                mock(Player.class, invocation -> true),
                mock(Player.class, invocation -> true)
        ));
        PerRoundAwardStrategy strategy = new PerRoundAwardStrategy();
        assertEquals(1, strategy.awardPoints(team));
    }

    @Test
    void awardPoints_ReturnsZeroWhenNotAllPlayersFoundBlock() {
        Team team = mock(Team.class);
        when(team.getPlayers()).thenReturn(Set.of(
                mock(Player.class, invocation -> true),
                mock(Player.class, invocation -> false)
        ));
        PerRoundAwardStrategy strategy = new PerRoundAwardStrategy();
        assertEquals(0, strategy.awardPoints(team));
    }
}