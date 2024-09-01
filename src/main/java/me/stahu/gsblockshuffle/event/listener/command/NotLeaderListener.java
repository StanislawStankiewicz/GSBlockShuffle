package me.stahu.gsblockshuffle.event.listener.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.command.NotLeaderHandler;
import me.stahu.gsblockshuffle.event.type.command.NotLeaderEvent;

@RequiredArgsConstructor
public class NotLeaderListener implements BlockShuffleEventListener<NotLeaderEvent> {

    final NotLeaderHandler notLeaderHandler;

    @Override
    public void onGameEvent(NotLeaderEvent event) {
        notLeaderHandler.sendNotLeaderMessage(event.sender());
    }
}
