package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.AddPlayerToTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.AddPlayerToTeamEvent;

@RequiredArgsConstructor
public class AddPlayerToTeamListener implements BlockShuffleEventListener<AddPlayerToTeamEvent> {

    final AddPlayerToTeamHandler addPlayerToTeamHandler;

    @Override
    public void onGameEvent(AddPlayerToTeamEvent event) {
        addPlayerToTeamHandler.sendAddPlayerToTeamMessages(event.team().getLeader(), event.player());
    }
}
