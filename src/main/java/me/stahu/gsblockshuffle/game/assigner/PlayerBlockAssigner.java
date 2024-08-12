package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.List;
import java.util.Set;

public class PlayerBlockAssigner implements BlockAssigner {

    @Override
    public void assignBlocks(Set<Team> teams, List<Block> blocks) {
        Block block;
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                block = blocks.get(random.nextInt(blocks.size()));
                player.setBlock(block);
            }
        }
    }
}
