package me.stahu.gsblockshuffle.event.listener.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.game.BlockFoundHandler;
import me.stahu.gsblockshuffle.event.type.game.BlockFoundEvent;

@RequiredArgsConstructor
public class BlockFoundListener implements BlockShuffleEventListener<BlockFoundEvent> {

    final BlockFoundHandler blockFoundHandler;

    @Override
    public void onGameEvent(BlockFoundEvent event) {
        blockFoundHandler.sendBlockFoundMessage(event.player(), event.block());
        blockFoundHandler.playBlockFoundSound(event.player());
    }
}
