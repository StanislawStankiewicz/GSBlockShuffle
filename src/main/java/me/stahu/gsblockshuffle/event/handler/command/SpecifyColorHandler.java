package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class SpecifyColorHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendSpecifyColorMessage(Player player) {
        messageController.commandResponse(player,
                messageBuilder.buildSpecifyColorMessage(), false);
    }
}
