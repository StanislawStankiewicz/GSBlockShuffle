package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class InvalidColorHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendInvalidColorMessage(Player player) {
        messageController.commandResponse(player,
                messageBuilder.buildInvalidColorMessage(), false);
    }
}
