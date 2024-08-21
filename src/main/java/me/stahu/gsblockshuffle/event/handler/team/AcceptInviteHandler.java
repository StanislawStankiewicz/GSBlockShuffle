package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class AcceptInviteHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendAcceptInviteSenderMessages(Team team, Player player) {
        messageController.sendMessage(player, messageBuilder.buildAcceptInviteSenderMessage(team));
        messageController.sendMessage(team.getLeader(), messageBuilder.buildAcceptInviteReceiverMessage(team, player));
    }
}
