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
        messageController.sendMessage(player, messageBuilder.buildAcceptInviteSenderMessage(team));
        messageController.sendMessage(team.getLeader(), messageBuilder.buildAcceptInviteReceiverMessage(team, player));
    }
}
