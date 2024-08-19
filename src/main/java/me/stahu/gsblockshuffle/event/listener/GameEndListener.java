package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.GameEndHandler;
import me.stahu.gsblockshuffle.event.type.GameEndEvent;

@RequiredArgsConstructor
public class GameEndListener implements GameEventListener<GameEndEvent> {

    final GameEndHandler gameEndHandler;

    @Override
    public void onGameEvent(GameEndEvent event) {
        gameEndHandler.sendGameEndMessage();
        gameEndHandler.sendEndScoresMessage(event.teams());
        gameEndHandler.playWinnerSound(event.teams());
    }
}
