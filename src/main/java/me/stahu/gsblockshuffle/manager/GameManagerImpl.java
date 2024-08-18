package me.stahu.gsblockshuffle.manager;

import lombok.Builder;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.event.type.*;
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
    public void invokeGameStart() {
        dispatcher.dispatch(new InvokeGameStartEvent());
    }

    @Override
    public void startGame() {
        round = 0;

        playersManager.assignTeams();

        dispatcher.dispatch(new GameStartEvent());
    }

    @Override
    public void newRound() {
        round++;
        blocks = blockSelector.getBlocks(difficulty);

        playersManager.resetBlocks();
        blockAssigner.assignBlocks(teams, blocks);

        dispatcher.dispatch(new RoundNewEvent());
    }

    @Override
    public void endRound() {
        teamEliminator.eliminateTeams(teams);
        difficulty = difficultyIncrementer.increaseDifficulty(difficulty, round);

        dispatcher.dispatch(new RoundEndEvent());
    }

    @Override
    public void roundBreak() {
        dispatcher.dispatch(new RoundBreakEvent());
    }

    @Override
    public void endBreak() {
        dispatcher.dispatch(new RoundBreakEndEvent());
    }

    @Override
    public void endGame() {
        dispatcher.dispatch(new GameEndEvent(teams));
    }

    @Override
    public boolean isRoundEnd() {
        return RoundEndConditionChecker.isRoundEndConditionMet(config, teams);
    }

    @Override
    public boolean isGameEnd() {
        return GameEndConditionChecker.isGameEnd(config, teams, round);
    }
}
