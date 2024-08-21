package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.*;

public class TeamBlockAssigner extends BlockAssigner {

    public TeamBlockAssigner(BlockShuffleEventDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void assignBlocks(Set<Team> teams, List<BlockPack> blocks) {
        Block block;
        for (Team team : teams) {
            block = blocks.get(random.nextInt(blocks.size()))
                    .blocks().get(random.nextInt(blocks.get(0).blocks().size()));
            for (Player player : team.getPlayers()) {
                assignBlock(player, block);
            }
        }
    }
}
