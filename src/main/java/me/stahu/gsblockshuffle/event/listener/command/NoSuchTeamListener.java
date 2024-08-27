package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NoSuchTeamHandler;
import me.stahu.gsblockshuffle.event.type.command.NoSuchTeamEvent;

@RequiredArgsConstructor
public class NoSuchTeamListener implements BlockShuffleEventListener<NoSuchTeamEvent> {

    final NoSuchTeamHandler noSuchTeamHandler;

    @Override
    public void onGameEvent(NoSuchTeamEvent event) {
        noSuchTeamHandler.sendNoSuchTeamMessage(event.sender(), event.teamName());
    }
}
