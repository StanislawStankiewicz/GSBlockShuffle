package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class SpecifyTeamNameHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendSpecifyTeamNameMessage(Player player) {
        messageController.commandResponse(player,
                messageBuilder.buildSpecifyTeamNameMessage(), false);
    }
}
