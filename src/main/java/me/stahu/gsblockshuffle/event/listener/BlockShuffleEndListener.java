package me.stahu.gsblockshuffle.event.listener;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventListener;
import me.stahu.gsblockshuffle.event.handler.GameEndHandler;
import me.stahu.gsblockshuffle.event.type.game.GameEndEvent;

@RequiredArgsConstructor
public class BlockShuffleEndListener implements BlockShuffleEventListener<GameEndEvent> {

    final GameEndHandler gameEndHandler;

    @Override
    public void onGameEvent(GameEndEvent event) {
        gameEndHandler.sendGameEndMessage();
        gameEndHandler.sendEndScoresMessage(event.teams());
        gameEndHandler.playWinnerSound(event.teams());
    }
}
