package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.*;

public class TeamBlockAssigner implements BlockAssigner {

    @Override
    public void assignBlocks(Set<Team> teams, List<Block> blocks) {
        Block block;
        for (Team team : teams) {
            block = blocks.get(random.nextInt(blocks.size()));
            for (Player player : team.getPlayers()) {
                player.setBlock(block);
            }
        }
    }
}
