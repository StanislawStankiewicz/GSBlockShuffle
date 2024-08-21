package me.stahu.gsblockshuffle.event.handler.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.sound.SoundPlayer;

@RequiredArgsConstructor
public class GameStartHandler {

    final MessageController messageController;
    final SoundPlayer soundPlayer;
    final MessageBuilder messageBuilder;

    public void sendGameStartMessage() {
        messageController.sendMessageToAll(messageBuilder.buildGameStartMessage());
    }

    public void playRoundCountDownSound(int roundStartDelaySeconds) {
        int startInSeconds = roundStartDelaySeconds - 2;
        if (startInSeconds >= 0) {
            soundPlayer.playRoundCountDownSound(startInSeconds * 20);
        }
    }
}
