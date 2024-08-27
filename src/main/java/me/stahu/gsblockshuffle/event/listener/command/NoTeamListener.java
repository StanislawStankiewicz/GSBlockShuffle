package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoTeamHandler;
import me.stahu.gsblockshuffle.event.type.command.NoTeamEvent;

@RequiredArgsConstructor
public class NoTeamListener implements BlockShuffleEventListener<NoTeamEvent> {

    final NoTeamHandler noTeamHandler;

    @Override
    public void onGameEvent(NoTeamEvent event) {
        noTeamHandler.sendNoTeamMessage(event.sender());
    }
}
