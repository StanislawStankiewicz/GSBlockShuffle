package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class AcceptRequestHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendAcceptRequestMessages(Player leader, Player player) {
        messageController.sendMessage(player, messageBuilder.buildAcceptRequestSenderMessage(leader.getTeam(), leader));
        messageController.sendMessage(leader, messageBuilder.buildAcceptRequestReceiverMessage(leader.getTeam(), player));
    }
}
