package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.config.BlockAssignmentMode;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


class BlockAssignerFactoryTests {

    BlockShuffleEventDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = mock(BlockShuffleEventDispatcher.class);
    }

    @Test
    void getBlockAssigner_ReturnsGameBlockAssignerForOnePerGameMode() {
        Config config = Config.builder()
                .blockAssignmentMode(BlockAssignmentMode.ONE_PER_GAME)
                .build();
        assertInstanceOf(GameBlockAssigner.class, BlockAssignerFactory.getBlockAssigner(config, dispatcher));
    }

    @Test
    void getBlockAssigner_ReturnsTeamBlockAssignerForOnePerTeamMode() {
        Config config = Config.builder()
                .blockAssignmentMode(BlockAssignmentMode.ONE_PER_TEAM)
                .build();
        assertInstanceOf(TeamBlockAssigner.class, BlockAssignerFactory.getBlockAssigner(config, dispatcher));
    }

    @Test
    void getBlockAssigner_ReturnsPlayerBlockAssignerForOnePerPlayerMode() {
        Config config = Config.builder()
                .blockAssignmentMode(BlockAssignmentMode.ONE_PER_PLAYER)
                .build();
        assertInstanceOf(PlayerBlockAssigner.class, BlockAssignerFactory.getBlockAssigner(config, dispatcher));
    }

    @Test
    void getBlockAssigner_ThrowsExceptionForNullConfig() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> BlockAssignerFactory.getBlockAssigner(null, null));
    }

    @Test
    void getBlockAssigner_ThrowsExceptionForInvalidMode() {
        Config config = Config.builder()
                .blockAssignmentMode(null)
                .build();
        assertThrows(NullPointerException.class, () -> BlockAssignerFactory.getBlockAssigner(config, dispatcher));
    }
}
