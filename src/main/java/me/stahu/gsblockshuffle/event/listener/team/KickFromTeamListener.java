package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.KickFromTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.KickFromTeamEvent;

@RequiredArgsConstructor
public class KickFromTeamListener implements BlockShuffleEventListener<KickFromTeamEvent> {

    final KickFromTeamHandler kickFromTeamHandler;

    @Override
    public void onGameEvent(KickFromTeamEvent event) {
        kickFromTeamHandler.sendKickFromTeamMessages(event.leader(), event.player());
    }
}
