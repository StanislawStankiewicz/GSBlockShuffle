package me.stahu.gsblockshuffle.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.game.BlockFoundEvent;
import me.stahu.gsblockshuffle.game.score.PointsAwarder;
import me.stahu.gsblockshuffle.manager.GameManager;
import me.stahu.gsblockshuffle.model.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

@RequiredArgsConstructor
public class GameController {

    private final GSBlockShuffle plugin;
    private final Config config;
    private final BlockShuffleEventDispatcher dispatcher;
    private final GameManager gameManager;
    private final PointsAwarder pointsAwarder;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private int currentTask;

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
        executeState(GameState.INVOKE_GAME_START, gameManager::invokeGameStart);
    }

    public void endGame() {
        executeState(GameState.GAME_END, gameManager::endGame);
    }

    private void nextState(GameState state) {
        switch (state) {
            case WAITING:
                break;
            case INVOKE_GAME_START:
                executeState(GameState.GAME_START, gameManager::startGame);
                break;
            case GAME_START, ROUND_BREAK_END:
                executeState(GameState.ROUND_NEW, gameManager::newRound);
                break;
            case ROUND_NEW:
                executeState(GameState.ROUND_END, gameManager::endRound);
                break;
            case ROUND_END:
                if (gameManager.isGameEnd()) {
                    endGame();
                } else {
                    executeState(GameState.ROUND_BREAK, gameManager::roundBreak);
                }
                break;
            case ROUND_BREAK:
                executeState(GameState.ROUND_BREAK_END, gameManager::endBreak);
                break;
            case GAME_END:
                executeState(GameState.WAITING, () -> {});
                break;
        }
    }

    public void executeState(GameState state, Runnable action) {
        scheduler.cancelTask(currentTask);
        setGameState(state);
        int duration = switch (state) {
            case INVOKE_GAME_START -> config.getGameStartDelaySeconds();
            case ROUND_NEW -> config.getRoundDurationSeconds();
            case ROUND_BREAK -> config.getBreakDurationSeconds();
            default -> 0;
        };
        action.run();
        currentTask = scheduler.scheduleSyncDelayedTask(plugin, () -> nextState(state), duration * 20L);
    }

    public void handlePlayerMoveEvent(Player player) {
        if (gameState != GameState.ROUND_NEW || player.isFoundBlock() || player.getTeam() == null) {
            return;
        }
        if (isPlayerOnAssignedBlock(player)) {
            player.setFoundBlock(true);
            player.getTeam()
                    .setScore(player.getTeam().getScore() + pointsAwarder.awardPoints(player.getTeam()));
            dispatcher.dispatch(new BlockFoundEvent(player, player.getAssignedBlock()));
        }
        if (gameManager.isRoundEnd()) {
            scheduler.cancelTask(currentTask);
            executeState(GameState.ROUND_END, gameManager::endRound);
        }
    }

    private boolean isPlayerOnAssignedBlock(Player player) {
        List<String> assignedBlockNames = player.getAssignedBlock().names();
        String playerBlockName = player.getApi().getBlockNameBelow(0);
        String belowPlayerBlockName = player.getApi().getBlockNameBelow(1);
        return assignedBlockNames.contains(playerBlockName) || assignedBlockNames.contains(belowPlayerBlockName);
    }
}
