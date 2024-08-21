package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.RequestToJoinTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.RequestToJoinTeamEvent;

@RequiredArgsConstructor
public class RequestToJoinTeamListener implements BlockShuffleEventListener<RequestToJoinTeamEvent> {

    final RequestToJoinTeamHandler requestToJoinTeamHandler;

    @Override
    public void onGameEvent(RequestToJoinTeamEvent event) {
        requestToJoinTeamHandler.sendRequestToJoinTeamMessages(event.player(), event.team());
    }
}
