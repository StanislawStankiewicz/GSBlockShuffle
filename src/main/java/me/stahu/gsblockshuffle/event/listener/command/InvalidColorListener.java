package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.InvalidColorHandler;
import me.stahu.gsblockshuffle.event.type.command.InvalidColorEvent;

@RequiredArgsConstructor
public class InvalidColorListener implements BlockShuffleEventListener<InvalidColorEvent> {

    final InvalidColorHandler invalidColorHandler;

    @Override
    public void onGameEvent(InvalidColorEvent event) {
        invalidColorHandler.sendInvalidColorMessage(event.sender());
    }
}
