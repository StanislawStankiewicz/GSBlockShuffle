package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.SpecifyTeamNameHandler;
import me.stahu.gsblockshuffle.event.type.command.SpecifyTeamNameEvent;

@RequiredArgsConstructor
public class SpecifyTeamNameListener implements BlockShuffleEventListener<SpecifyTeamNameEvent> {

    final SpecifyTeamNameHandler specifyTeamNameHandler;

    @Override
    public void onGameEvent(SpecifyTeamNameEvent event) {
        specifyTeamNameHandler.sendSpecifyTeamNameMessage(event.sender());
    }
}
