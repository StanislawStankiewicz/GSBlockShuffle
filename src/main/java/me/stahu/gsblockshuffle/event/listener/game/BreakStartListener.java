package me.stahu.gsblockshuffle.event.listener.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.game.BreakStartHandler;
import me.stahu.gsblockshuffle.event.type.game.BreakStartEvent;

@RequiredArgsConstructor
public class BreakStartListener implements BlockShuffleEventListener<BreakStartEvent> {

    final BreakStartHandler breakStartHandler;

    @Override
    public void onGameEvent(BreakStartEvent event) {
        breakStartHandler.playRoundCountDownSound(event.roundStartDelaySeconds());
    }
}
