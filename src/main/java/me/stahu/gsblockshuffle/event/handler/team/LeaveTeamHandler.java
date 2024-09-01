package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class LeaveTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendLeaveTeamMessages(Team team, Player player) {
        messageController.commandResponse(player, messageBuilder.buildLeaveTeamMessage(team), true);
    }
}
