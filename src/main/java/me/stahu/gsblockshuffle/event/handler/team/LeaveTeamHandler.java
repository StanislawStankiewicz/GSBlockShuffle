package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class LeaveTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendLeaveTeamMessages(Player player) {
        messageController.sendMessage(player, messageBuilder.buildLeaveTeamMessage(player.getTeam()));
    }
}
