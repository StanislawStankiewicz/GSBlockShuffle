package me.stahu.gsblockshuffle.game.assigner;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockAssignEvent;
import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BlockAssigner {

    protected final GameEventDispatcher dispatcher;
    protected Random random = new Random();

    public abstract void assignBlocks(Set<Team> teams, List<BlockPack> blocks);

    void assignBlock(Player player, Block block) {
        player.setAssignedBlock(block);
        dispatcher.dispatch(new BlockAssignEvent(player, block));
    }
}
