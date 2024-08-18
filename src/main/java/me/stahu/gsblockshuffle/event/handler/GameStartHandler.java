package me.stahu.gsblockshuffle.event.handler;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

@RequiredArgsConstructor
public class GameStartHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendGameStartMessage() {
        messageController.sendMessageToAll(messageBuilder.buildGameStartMessage());
    }
}
