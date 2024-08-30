package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class KickFromTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendKickFromTeamMessages(Player leader, Player player) {
        messageController.commandResponse(leader, messageBuilder.buildKickFromTeamSenderMessage(leader.getTeam(), player), true);
        messageController.commandResponse(player, messageBuilder.buildKickFromTeamReceiverMessage(leader.getTeam()), true);
    }
}
