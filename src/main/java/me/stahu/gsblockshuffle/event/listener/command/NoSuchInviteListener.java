package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoSuchInviteHandler;
import me.stahu.gsblockshuffle.event.type.command.NoSuchInviteEvent;

@RequiredArgsConstructor
public class NoSuchInviteListener implements BlockShuffleEventListener<NoSuchInviteEvent> {

    final NoSuchInviteHandler noSuchInviteHandler;

    @Override
    public void onGameEvent(NoSuchInviteEvent event) {
        noSuchInviteHandler.sendNoSuchInviteMessage(event.sender());
    }
}
