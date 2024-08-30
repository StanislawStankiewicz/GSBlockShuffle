package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeBlocks;
import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.*;

class GameBlockAssignerTests {

    private GameBlockAssigner assigner;

    @BeforeEach
    void setUp() {
        BlockShuffleEventDispatcher dispatcher = Mockito.mock(BlockShuffleEventDispatcher.class);
        assigner = new GameBlockAssigner(dispatcher);
    }

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        Set<Team> teams = arrangeTeams(3, 3);
        List<BlockPack> blocks = arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        Block chosenBlock = teams.iterator().next().getPlayers().iterator().next().getAssignedBlock();
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                assertEquals(chosenBlock, player.getAssignedBlock());
            }
        }
    }

    @Test
    void assignBlocks_EmptyTeamsSet() {
        Set<Team> teams = arrangeTeams(0, 0);
        List<BlockPack> blocks = arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                assertNull(player.getAssignedBlock());
            }
        }
    }

    @Test
    void assignBlocks_EmptyBlocksList() {
        Set<Team> teams = arrangeTeams(3, 3);
        List<BlockPack> blocks = arrangeBlocks(0);

        assertThrows(IllegalArgumentException.class, () -> assigner.assignBlocks(teams, blocks));
    }

    @Test
    void assignBlocks_NullTeamsSet() {
        List<BlockPack> blocks = arrangeBlocks(3);
        // noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> assigner.assignBlocks(null, blocks));
    }

    @Test
    void assignBlocks_NullBlocksList() {
        Set<Team> teams = arrangeTeams(3, 3);
        // noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> assigner.assignBlocks(teams, null));
    }
}