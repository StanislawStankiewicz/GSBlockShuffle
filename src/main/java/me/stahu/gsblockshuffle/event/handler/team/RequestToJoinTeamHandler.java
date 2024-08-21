package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class RequestToJoinTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendRequestToJoinTeamMessages(Player player, Team team) {
        messageController.sendMessage(player, messageBuilder.buildRequestToJoinTeamSenderMessage(team, player));
        messageController.sendMessage(team.getLeader(), messageBuilder.buildRequestToJoinTeamReceiverMessage(team, player));
    }
}
