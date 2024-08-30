package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class AddPlayerToTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendAddPlayerToTeamMessages(Player leader, Player player) {
        messageController.commandResponse(player, messageBuilder.buildAddPlayerToTeamSenderMessage(leader.getTeam(), leader), true);
        messageController.commandResponse(leader, messageBuilder.buildAddPlayerToTeamReceiverMessage(leader.getTeam(), player), true);
    }
}
