package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.SpecifyPlayerHandler;
import me.stahu.gsblockshuffle.event.type.command.SpecifyPlayerEvent;

@RequiredArgsConstructor
public class SpecifyPlayerListener implements BlockShuffleEventListener<SpecifyPlayerEvent> {

    final SpecifyPlayerHandler specifyPlayerHandler;

    @Override
    public void onGameEvent(SpecifyPlayerEvent event) {
        specifyPlayerHandler.sendSpecifyPlayerMessage(event.sender());
    }
}
