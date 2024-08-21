package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.RemoveTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.RemoveTeamEvent;

@RequiredArgsConstructor
public class RemoveTeamListener implements BlockShuffleEventListener<RemoveTeamEvent> {

    RemoveTeamHandler removeTeamHandler;

    @Override
    public void onGameEvent(RemoveTeamEvent event) {
        removeTeamHandler.sendRemoveTeamMessages(event.team().getLeader());
    }
}
