package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.AlreadyInTeamHandler;
import me.stahu.gsblockshuffle.event.type.command.AlreadyInTeamEvent;

@RequiredArgsConstructor
public class AlreadyInTeamListener implements BlockShuffleEventListener<AlreadyInTeamEvent> {

    final AlreadyInTeamHandler alreadyInTeamHandler;

    @Override
    public void onGameEvent(AlreadyInTeamEvent event) {
        alreadyInTeamHandler.sendAlreadyInTeamMessage(event.sender());
    }
}
