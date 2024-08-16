package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.List;
import java.util.Set;

public class GameBlockAssigner implements BlockAssigner {

    @Override
    public void assignBlocks(Set<Team> teams, List<BlockPack> blocks) {
        Block block = blocks.get(random.nextInt(blocks.size()))
                .blocks().get(random.nextInt(blocks.get(0).blocks().size()));
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                player.setAssignedBlock(block);
            }
        }
    }
}
