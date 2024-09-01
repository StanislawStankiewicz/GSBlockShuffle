package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class NoSuchPlayerHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendNoSuchPlayerMessage(Player player, String playerName) {
        messageController.commandResponse(player,
                messageBuilder.buildNoSuchPlayerMessage(playerName), false);
    }
}
