package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class RemoveTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendRemoveTeamMessages(Player leader) {
        messageController.sendMessage(leader, messageBuilder.buildRemoveTeamMessage(leader.getTeam()));
    }
}
