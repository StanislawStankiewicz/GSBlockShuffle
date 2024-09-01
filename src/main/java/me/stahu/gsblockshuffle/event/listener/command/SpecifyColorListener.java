package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.SpecifyColorHandler;
import me.stahu.gsblockshuffle.event.type.command.SpecifyColorEvent;

@RequiredArgsConstructor
public class SpecifyColorListener implements BlockShuffleEventListener<SpecifyColorEvent> {

    final SpecifyColorHandler specifyColorHandler;

    @Override
    public void onGameEvent(SpecifyColorEvent event) {
        specifyColorHandler.sendSpecifyColorMessage(event.sender());
    }
}
