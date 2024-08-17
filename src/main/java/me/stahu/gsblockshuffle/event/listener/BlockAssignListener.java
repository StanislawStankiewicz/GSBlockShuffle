package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockAssignEvent;
import me.stahu.gsblockshuffle.event.GameEvent;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.BlockAssignHandler;

@RequiredArgsConstructor
public class BlockAssignListener implements GameEventListener {

    final BlockAssignHandler blockAssignHandler;

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof BlockAssignEvent blockAssignEvent) {
            blockAssignHandler.sendAssignmentMessage(blockAssignEvent);
        }
    }
}
