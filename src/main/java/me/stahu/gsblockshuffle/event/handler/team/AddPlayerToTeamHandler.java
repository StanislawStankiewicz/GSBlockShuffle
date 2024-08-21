package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class AddPlayerToTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendAddPlayerToTeamMessages(Player leader, Player player) {
        messageController.sendMessage(player, messageBuilder.buildAddPlayerToTeamSenderMessage(leader.getTeam(), leader));
        messageController.sendMessage(leader, messageBuilder.buildAddPlayerToTeamReceiverMessage(leader.getTeam(), player));
    }
}
