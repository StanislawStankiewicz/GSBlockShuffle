package me.stahu.gsblockshuffle.event.listener.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.team.LeaveTeamHandler;
import me.stahu.gsblockshuffle.event.type.team.LeaveTeamEvent;

@RequiredArgsConstructor
public class LeaveTeamListener implements BlockShuffleEventListener<LeaveTeamEvent> {

    final LeaveTeamHandler leaveTeamHandler;

    @Override
    public void onGameEvent(LeaveTeamEvent event) {
        leaveTeamHandler.sendLeaveTeamMessages(event.team(), event.player());
    }
}
