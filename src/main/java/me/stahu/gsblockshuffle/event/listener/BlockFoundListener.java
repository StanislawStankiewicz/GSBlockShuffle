package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.BlockFoundHandler;
import me.stahu.gsblockshuffle.event.type.BlockFoundEvent;

@RequiredArgsConstructor
public class BlockFoundListener implements GameEventListener<BlockFoundEvent> {

    final BlockFoundHandler blockFoundHandler;

    @Override
    public void onGameEvent(BlockFoundEvent event) {
        blockFoundHandler.sendBlockFoundMessage(event.player(), event.block());
    }
}
