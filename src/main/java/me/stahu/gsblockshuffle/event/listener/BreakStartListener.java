package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.BreakStartHandler;
import me.stahu.gsblockshuffle.event.type.BreakStartEvent;

@RequiredArgsConstructor
public class BreakStartListener implements GameEventListener<BreakStartEvent> {

    final BreakStartHandler breakStartHandler;

    @Override
    public void onGameEvent(BreakStartEvent event) {
        breakStartHandler.playRoundCountDownSound(event.roundStartDelaySeconds());
    }
}
