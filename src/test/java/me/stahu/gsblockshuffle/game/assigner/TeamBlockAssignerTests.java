package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeBlocks;
import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TeamBlockAssignerTests {

    GameEventDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = mock(GameEventDispatcher.class);
    }

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        TeamBlockAssigner assigner = new TeamBlockAssigner(dispatcher);

        Set<Team> teams = arrangeTeams(3, 3);
        List<BlockPack> blocks = arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        Block chosenBlock;
        for (Team team : teams) {
            chosenBlock = team.getPlayers().iterator().next().getAssignedBlock();
            for (Player player : team.getPlayers()) {
                assertEquals(chosenBlock, player.getAssignedBlock());
            }
        }
    }
}