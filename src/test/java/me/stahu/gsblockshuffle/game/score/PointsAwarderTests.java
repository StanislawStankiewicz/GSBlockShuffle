package me.stahu.gsblockshuffle.game.score;

import me.stahu.gsblockshuffle.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsAwarderTests {

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
}
