package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.InvokeGameStartHandler;
import me.stahu.gsblockshuffle.event.type.InvokeGameStartEvent;

@RequiredArgsConstructor
public class InvokeGameStartListener implements GameEventListener<InvokeGameStartEvent> {

    final InvokeGameStartHandler invokeGameStartHandler;

    @Override
    public void onGameEvent(InvokeGameStartEvent event) {
        invokeGameStartHandler.playRoundCountDownSound(event.roundStartDelaySeconds());
    }
}
