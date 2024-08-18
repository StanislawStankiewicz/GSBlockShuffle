package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockAssignEvent;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.BlockAssignHandler;

@RequiredArgsConstructor
public class BlockAssignListener implements GameEventListener<BlockAssignEvent> {

    final BlockAssignHandler blockAssignHandler;

    @Override
    public void onGameEvent(BlockAssignEvent event) {
        blockAssignHandler.sendAssignmentMessage(event.player(), event.block());
    }
}
