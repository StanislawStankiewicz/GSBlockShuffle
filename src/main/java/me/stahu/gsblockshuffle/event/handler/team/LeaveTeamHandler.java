package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class LeaveTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendLeaveTeamMessages(Player player) {
        messageController.sendMessage(player, messageBuilder.buildLeaveTeamMessage(player.getTeam()));
    }
}
