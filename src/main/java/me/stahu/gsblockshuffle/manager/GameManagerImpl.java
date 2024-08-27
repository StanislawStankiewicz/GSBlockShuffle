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
import java.util.Optional;
import java.util.Set;

@Builder
public class GameManagerImpl implements GameManager {

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

    @Override
    public void invokeGameStart() {
        dispatcher.dispatch(new InvokeGameStartEvent(config.getGameStartDelaySeconds()));
    }

    @Override
    public void startGame() {
        round = 0;
        difficulty = config.getStartDifficulty();

        playerManager.assignDefaultTeams();
        teams.forEach(team -> team.setScore(0));

        dispatcher.dispatch(new GameStartEvent());
    }

    @Override
    public void newRound() {
        round++;
        blocks = blockSelector.getBlocks(difficulty);

//        System.out.println("Round " + round);
//        blocks.forEach(block -> System.out.println(block.blocks().get(0).toString()));

        teams.forEach(team -> team.getPlayers()
                .forEach(player -> {
                    player.setAssignedBlock(Optional.empty());
                    player.setFoundBlock(false);
                }));
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
        dispatcher.dispatch(new BreakStartEvent(config.getBreakDurationSeconds()));
    }

    @Override
    public void endBreak() {
        dispatcher.dispatch(new BreakEndEvent());
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
