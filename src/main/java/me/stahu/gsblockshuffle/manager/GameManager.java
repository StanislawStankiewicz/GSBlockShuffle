package me.stahu.gsblockshuffle.manager;

import lombok.Builder;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.game.*;
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
public class GameManager {

    final BlockShuffleEventDispatcher dispatcher;
    final PlayerManager playerManager;
    final Config config;
    DifficultyIncrementer difficultyIncrementer;
    BlockSelector blockSelector;
    BlockAssigner blockAssigner;
    TeamEliminator teamEliminator;

    final Set<Team> teams;
    List<BlockPack> blocks;
    int round;
    int difficulty;

    public void invokeGameStart() {
        dispatcher.dispatch(new InvokeGameStartEvent(config.getGameStartDelaySeconds()));
    }

    public void startGame() {
        round = 0;
        difficulty = config.getStartDifficulty();

        playerManager.assignDefaultTeams();
        teams.forEach(team -> team.setScore(0));

        dispatcher.dispatch(new GameStartEvent());
    }

    public void newRound() {
        round++;
        blocks = blockSelector.getBlocks(difficulty);

        teams.forEach(team -> team.getPlayers()
                .forEach(player -> {
                    player.setAssignedBlock(null);
                    player.setFoundBlock(false);
                }));
        blockAssigner.assignBlocks(teams, blocks);

        dispatcher.dispatch(new RoundNewEvent());
    }

    public void endRound() {
        teamEliminator.eliminateTeams(teams);
        difficulty = difficultyIncrementer.increaseDifficulty(difficulty, round);

        dispatcher.dispatch(new RoundEndEvent());
    }

    public void roundBreak() {
        dispatcher.dispatch(new BreakStartEvent(config.getBreakDurationSeconds()));
    }

    public void endBreak() {
        dispatcher.dispatch(new BreakEndEvent());
    }

    public void endGame() {
        dispatcher.dispatch(new GameEndEvent(teams));
    }

    public boolean isRoundEnd() {
        return RoundEndConditionChecker.isRoundEndConditionMet(config, teams);
    }

    public boolean isGameEnd() {
        return GameEndConditionChecker.isGameEnd(config, teams, round);
    }
}
