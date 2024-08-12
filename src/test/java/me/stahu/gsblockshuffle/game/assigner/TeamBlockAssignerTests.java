package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamBlockAssignerTests {

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        TeamBlockAssigner assigner = new TeamBlockAssigner();

        Set<Team> teams = AssignerTestsArranger.arrangeTeams(3, 3);
        List<Block> blocks = AssignerTestsArranger.arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        Block chosenBlock;
        for (Team team : teams) {
            chosenBlock = team.getPlayers().iterator().next().getBlock();
            for (Player player : team.getPlayers()) {
                assertEquals(chosenBlock, player.getBlock());
            }
        }
    }
}