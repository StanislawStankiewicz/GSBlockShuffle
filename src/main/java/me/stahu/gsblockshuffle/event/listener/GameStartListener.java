package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.handler.GameStartHandler;
import me.stahu.gsblockshuffle.event.type.GameStartEvent;

@RequiredArgsConstructor
public class GameStartListener implements GameEventListener<GameStartEvent> {

    final GameStartHandler gameStartHandler;

    @Override
    public void onGameEvent(GameStartEvent event) {
        gameStartHandler.sendGameStartMessage();
    }
}
