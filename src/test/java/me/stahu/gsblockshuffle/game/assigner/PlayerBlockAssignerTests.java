package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerBlockAssignerTests {

    @Test
    void assignBlocks_AssignsBlocksToPlayers() {
        PlayerBlockAssigner assigner = new PlayerBlockAssigner();

        Set<Team> teams = AssignerTestsArranger.arrangeTeams(3, 3);
        List<Block> blocks = AssignerTestsArranger.arrangeBlocks(3);

        assigner.assignBlocks(teams, blocks);

        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                assertNotNull(player.getBlock());
            }
        }
    }
}