package me.stahu.gsblockshuffle.event.listener.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.game.GameStartHandler;
import me.stahu.gsblockshuffle.event.type.game.GameStartEvent;

@RequiredArgsConstructor
public class BlockShuffleStartListener implements BlockShuffleEventListener<GameStartEvent> {

    final GameStartHandler gameStartHandler;

    @Override
    public void onGameEvent(GameStartEvent event) {
        gameStartHandler.sendGameStartMessage();
    }
}
