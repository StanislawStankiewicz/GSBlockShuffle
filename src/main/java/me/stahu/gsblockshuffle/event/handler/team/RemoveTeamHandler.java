package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

public class RemoveTeamHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendRemoveTeamMessages(Player leader) {
        messageController.sendMessage(leader, messageBuilder.buildRemoveTeamMessage(leader.getTeam()));
    }
}
