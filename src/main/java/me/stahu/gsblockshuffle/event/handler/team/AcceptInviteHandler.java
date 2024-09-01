package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class AcceptInviteHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendAcceptInviteSenderMessages(Team team, Player player) {
        messageController.commandResponse(player, messageBuilder.buildAcceptInviteSenderMessage(team), true);
        messageController.commandResponse(team.getLeader(), messageBuilder.buildAcceptInviteReceiverMessage(team, player), true);
    }
}
