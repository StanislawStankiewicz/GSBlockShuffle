package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoSuchRequestHandler;
import me.stahu.gsblockshuffle.event.type.command.NoSuchRequestEvent;

@RequiredArgsConstructor
public class NoSuchRequestListener implements BlockShuffleEventListener<NoSuchRequestEvent> {

    final NoSuchRequestHandler noSuchRequestHandler;

    @Override
    public void onGameEvent(NoSuchRequestEvent event) {
        noSuchRequestHandler.sendNoSuchRequestMessage(event.sender());
    }
}
