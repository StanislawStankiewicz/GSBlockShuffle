package me.stahu.gsblockshuffle.event.handler.team;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class CreateTeamHandler {
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendCreateTeamMessages(Player leader) {
        messageController.commandResponse(leader, messageBuilder.buildCreateTeamMessage(leader.getTeam()), true);
    }
}
