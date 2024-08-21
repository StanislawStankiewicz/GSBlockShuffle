package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.AcceptRequestHandler;
import me.stahu.gsblockshuffle.event.type.team.AcceptRequestEvent;

@RequiredArgsConstructor
public class AcceptRequestListener implements BlockShuffleEventListener<AcceptRequestEvent> {

    final AcceptRequestHandler acceptRequestHandler;

    @Override
    public void onGameEvent(AcceptRequestEvent event) {
        acceptRequestHandler.sendAcceptRequestMessages(event.leader(), event.player());
    }
}
