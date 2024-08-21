package me.stahu.gsblockshuffle.event.handler.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class BlockAssignHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendAssignmentMessage(Player player, Block block) {
        messageController.sendMessage(player,
                messageBuilder.buildBlockAssignmentMessage(block));
    }
}
