package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeBlocks;
import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlayerBlockAssignerTests {

    GameEventDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = mock(GameEventDispatcher.class);
    }

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        PlayerBlockAssigner assigner = new PlayerBlockAssigner(dispatcher);

        Set<Team> teams = arrangeTeams(3, 3);
        List<BlockPack> blocks = arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                assertNotNull(player.getAssignedBlock());
            }
        }
    }
}