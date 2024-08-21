package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.AcceptInviteHandler;
import me.stahu.gsblockshuffle.event.type.team.AcceptInviteEvent;

@RequiredArgsConstructor
public class AcceptInviteListener implements BlockShuffleEventListener<AcceptInviteEvent> {

    final AcceptInviteHandler acceptInviteHandler;

    @Override
    public void onGameEvent(AcceptInviteEvent event) {
        acceptInviteHandler.sendAcceptInviteSenderMessages(event.team(), event.player());
    }
}
