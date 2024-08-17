package me.stahu.gsblockshuffle.manager;

import lombok.Builder;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.GameEvent;
import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.event.GameEventType;
import me.stahu.gsblockshuffle.game.assigner.BlockAssigner;
import me.stahu.gsblockshuffle.game.blocks.BlockSelector;
import me.stahu.gsblockshuffle.game.difficulty.DifficultyIncrementer;
import me.stahu.gsblockshuffle.game.eliminator.TeamEliminator;
import me.stahu.gsblockshuffle.game.round.GameEndConditionChecker;
import me.stahu.gsblockshuffle.game.round.RoundEndConditionChecker;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Team;

import java.util.List;
import java.util.Set;

@Builder
public class GameManagerImpl implements GameManager {

    final GameEventDispatcher dispatcher;
    final Config config;
    final PlayersManager playersManager;
    DifficultyIncrementer difficultyIncrementer;
    BlockSelector blockSelector;
    BlockAssigner blockAssigner;
    TeamEliminator teamEliminator;

    final Set<Team> teams;
    List<BlockPack> blocks;
    int round;
    int difficulty;

    @Override
    public void startGame() {
        round = 0;

        playersManager.assignTeams();

        dispatchEvent(GameEventType.GAME_START);
    }

    @Override
    public void newRound() {
        round++;
        blocks = blockSelector.getBlocks(difficulty);

        playersManager.resetBlocks();
        blockAssigner.assignBlocks(teams, blocks);

        dispatchEvent(GameEventType.ROUND_NEW);
    }

    @Override
    public void endRound() {
        teamEliminator.eliminateTeams(teams);
        if (isGameEnd()) {
            dispatchEvent(GameEventType.GAME_END);
            return;
        }
        difficulty = difficultyIncrementer.increaseDifficulty(difficulty, round);

        dispatchEvent(GameEventType.ROUND_END);
    }

    @Override
    public void roundBreak() {
        dispatchEvent(GameEventType.ROUND_BREAK);
    }

    @Override
    public void endBreak() {
        dispatchEvent(GameEventType.ROUND_BREAK_END);
    }

    @Override
    public void endGame() {
        dispatchEvent(GameEventType.GAME_END);
    }

    @Override
    public boolean isRoundEnd() {
        return RoundEndConditionChecker.isRoundEndConditionMet(config, teams);
    }

    @Override
    public boolean isGameEnd() {
        return GameEndConditionChecker.isGameEnd(config, teams, round);
    }

    private void dispatchEvent(GameEventType eventType) {
        dispatcher.dispatch(new GameEvent(eventType));
    }
}
