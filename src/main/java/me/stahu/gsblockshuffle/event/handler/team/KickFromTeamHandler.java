package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class KickFromTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendKickFromTeamMessages(Player leader, Player player) {
        messageController.sendMessage(leader, messageBuilder.buildKickFromTeamSenderMessage(leader.getTeam(), player));
        messageController.sendMessage(player, messageBuilder.buildKickFromTeamReceiverMessage(leader.getTeam()));
    }
}
