package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeBlocks;
import static me.stahu.gsblockshuffle.game.assigner.AssignerTestsArranger.arrangeTeams;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBlockAssignerTests {

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        GameBlockAssigner assigner = new GameBlockAssigner();

        Set<Team> teams = arrangeTeams(3, 3);
        List<BlockPack> blocks = arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        Block chosenBlock = teams.iterator().next().getPlayers().iterator().next().getBlock();
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                assertEquals(chosenBlock, player.getBlock());
            }
        }
    }
}