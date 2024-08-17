package me.stahu.gsblockshuffle.event;

import lombok.Getter;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;


@Getter
public class BlockAssignEvent extends GameEvent {
    final Player player;
    final Block block;

    public BlockAssignEvent(Player player, Block block) {
        super(GameEventType.BLOCK_ASSIGN);
        this.player = player;
        this.block = block;
    }
}
