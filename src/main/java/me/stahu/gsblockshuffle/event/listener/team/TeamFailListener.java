package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.TeamFailHandler;
import me.stahu.gsblockshuffle.event.type.team.TeamFailEvent;

@RequiredArgsConstructor
public class TeamFailListener implements BlockShuffleEventListener<TeamFailEvent> {

    final TeamFailHandler teamFailHandler;

    @Override
    public void onGameEvent(TeamFailEvent event) {
        teamFailHandler.sendTeamFailMessages(event.player(), event.reason());
    }
}
