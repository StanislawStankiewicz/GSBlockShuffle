package me.stahu.gsblockshuffle.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

@RequiredArgsConstructor
public class GameController {

    private final GSBlockShuffle plugin;
    private final Config config;
    private final GameManager gameManager;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    @Getter
    private GameState gameState;

    public void setGameState(GameState newState) {
        if (isValidTransition(newState)) {
            this.gameState = newState;
        } else {
            throw new IllegalStateException("Invalid state transition: " + gameState + " to " + newState);
        }
    }

    private boolean isValidTransition(GameState newState) {
        return true;
    }

    public void startGame() {
        nextState(GameState.WAITING);
    }

    private void nextState(GameState state) {
        switch (state) {
            case WAITING:
                executeState(GameState.GAME_START, gameManager::startGame);
                break;
            case GAME_START:
                executeState(GameState.ROUND_NEW, gameManager::newRound);
                break;
            case ROUND_NEW:
                executeState(GameState.ROUND_END, gameManager::endRound);
                break;
            case ROUND_END:
                if (gameManager.isGameEnd()) {
                    executeState(GameState.GAME_END, gameManager::endGame);
                } else {
                    executeState(GameState.ROUND_BREAK, gameManager::newRound);
                }
                break;
            case ROUND_BREAK:
                executeState(GameState.ROUND_BREAK_END, gameManager::roundBreak);
                break;
            case ROUND_BREAK_END:
                executeState(GameState.ROUND_NEW, gameManager::endBreak);
                break;
            case GAME_END:
                executeState(GameState.WAITING, gameManager::endGame);
                break;
        }
    }

    private void executeState(GameState state, Runnable action) {
        setGameState(state);
        action.run();
        int duration = switch (state) {
            case ROUND_NEW -> config.getRoundDurationSeconds();
            case ROUND_BREAK -> config.getBreakDurationSeconds();
            default -> 0;
        };
        scheduler.scheduleSyncDelayedTask(plugin, () -> nextState(state), duration * 20L);
    }
}
