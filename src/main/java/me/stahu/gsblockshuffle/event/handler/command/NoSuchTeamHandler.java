package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class NoSuchTeamHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendNoSuchTeamMessage(Player player, String teamName) {
        messageController.commandResponse(player,
                messageBuilder.buildNoSuchTeamMessage(teamName), false);
    }
}
