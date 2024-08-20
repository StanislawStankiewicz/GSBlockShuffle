package me.stahu.gsblockshuffle.event.listener.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.game.InvokeGameStartHandler;
import me.stahu.gsblockshuffle.event.type.game.InvokeGameStartEvent;

@RequiredArgsConstructor
public class InvokeBlockShuffleStartListener implements BlockShuffleEventListener<InvokeGameStartEvent> {

    final InvokeGameStartHandler invokeGameStartHandler;

    @Override
    public void onGameEvent(InvokeGameStartEvent event) {
        invokeGameStartHandler.playRoundCountDownSound(event.roundStartDelaySeconds());
    }
}
