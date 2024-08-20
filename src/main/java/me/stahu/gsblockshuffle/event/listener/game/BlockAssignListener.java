package me.stahu.gsblockshuffle.event.listener.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.type.game.BlockAssignEvent;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.game.BlockAssignHandler;

@RequiredArgsConstructor
public class BlockAssignListener implements BlockShuffleEventListener<BlockAssignEvent> {

    final BlockAssignHandler blockAssignHandler;

    @Override
    public void onGameEvent(BlockAssignEvent event) {
        blockAssignHandler.sendAssignmentMessage(event.player(), event.block());
    }
}
