package me.stahu.gsblockshuffle.manager;

public interface GameManager {

    void startGame();

    void newRound();

    void endRound();

    void roundBreak();

    void endBreak();

    void endGame();

    boolean isRoundEnd();

    boolean isGameEnd();
}
