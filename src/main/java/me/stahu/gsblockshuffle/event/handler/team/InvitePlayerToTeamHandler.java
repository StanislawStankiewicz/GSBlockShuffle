package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class InvitePlayerToTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendInvitePlayerToTeamMessages(Player leader, Player player) {
        messageController.sendMessage(player, messageBuilder.buildInvitePlayerToTeamSenderMessage(leader.getTeam(), leader));
        messageController.sendMessage(leader, messageBuilder.buildInvitePlayerToTeamReceiverMessage(leader.getTeam(), player));
    }
}