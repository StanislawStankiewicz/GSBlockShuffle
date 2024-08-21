package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.CreateTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.CreateTeamEvent;

@RequiredArgsConstructor
public class CreateTeamListener implements BlockShuffleEventListener<CreateTeamEvent> {

    final CreateTeamHandler createTeamHandler;

    @Override
    public void onGameEvent(CreateTeamEvent event) {
        createTeamHandler.sendCreateTeamMessages(event.team().getLeader());
    }
}
