package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoSuchPlayerHandler;
import me.stahu.gsblockshuffle.event.type.command.NoSuchPlayerEvent;

@RequiredArgsConstructor
public class NoSuchPlayerListener implements BlockShuffleEventListener<NoSuchPlayerEvent> {

    final NoSuchPlayerHandler noSuchPlayerHandler;

    @Override
    public void onGameEvent(NoSuchPlayerEvent event) {
        noSuchPlayerHandler.sendNoSuchPlayerMessage(event.sender(), event.playerName());
    }
}
