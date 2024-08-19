package me.stahu.gsblockshuffle.event.handler;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.view.sound.SoundPlayer;

@RequiredArgsConstructor
public class BreakStartHandler {

    final SoundPlayer soundPlayer;

    public void playRoundCountDownSound(int roundStartDelaySeconds) {
        int startInSeconds = roundStartDelaySeconds - 2;
        if (startInSeconds >= 0) {
            soundPlayer.playRoundCountDownSound(startInSeconds * 20);
        }
    }
}
