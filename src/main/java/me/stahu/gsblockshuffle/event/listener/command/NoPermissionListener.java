package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoPermissionHandler;
import me.stahu.gsblockshuffle.event.type.command.NoPermissionEvent;

@RequiredArgsConstructor
public class NoPermissionListener implements BlockShuffleEventListener<NoPermissionEvent> {

    final NoPermissionHandler noPermissionHandler;

    @Override
    public void onGameEvent(NoPermissionEvent event) {
        noPermissionHandler.sendNoPermissionMessage(event.sender());
    }
}
