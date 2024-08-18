package me.stahu.gsblockshuffle.event.handler;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class BlockFoundHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendBlockFoundMessage(Player player, Block block) {
        messageController.sendMessageToAll(
                messageBuilder.buildBlockFoundMessage(player, block));
    }
}
