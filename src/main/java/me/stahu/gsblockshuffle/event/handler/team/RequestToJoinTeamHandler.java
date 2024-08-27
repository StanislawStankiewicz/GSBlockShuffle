package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class RequestToJoinTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendRequestToJoinTeamMessages(Player player, Team team) {
        messageController.commandResponse(player, messageBuilder.buildRequestToJoinTeamSenderMessage(team, player), true);
        messageController.commandResponse(team.getLeader(), messageBuilder.buildRequestToJoinTeamReceiverMessage(team, player), true);
    }
}
