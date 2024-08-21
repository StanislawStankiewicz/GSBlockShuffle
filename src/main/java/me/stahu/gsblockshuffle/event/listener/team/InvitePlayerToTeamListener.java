package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.InvitePlayerToTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.InvitePlayerToTeamEvent;

@RequiredArgsConstructor
public class InvitePlayerToTeamListener implements BlockShuffleEventListener<InvitePlayerToTeamEvent> {

    final InvitePlayerToTeamHandler invitePlayerToTeamHandler;

    @Override
    public void onGameEvent(InvitePlayerToTeamEvent event) {
        invitePlayerToTeamHandler.sendInvitePlayerToTeamMessages(event.inviter(), event.player());
    }
}
