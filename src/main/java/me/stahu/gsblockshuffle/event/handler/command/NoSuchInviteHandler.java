package me.stahu.gsblockshuffle.event.handler.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class NoSuchInviteHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendNoSuchInviteMessage(Player player) {
        messageController.commandResponse(player,
                messageBuilder.buildNoSuchInviteMessage(player), false);
    }
}
