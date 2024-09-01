package me.stahu.gsblockshuffle.game.round;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class RoundEndConditionChecker {

    private RoundEndConditionChecker() {
    }

    public static boolean isRoundEndConditionMet(Config config, Set<Team> teams) {
        boolean allPlayersRequiredForTeamWinCondition = config.isAllPlayersRequiredForTeamWin();
        boolean firstToWinCondition = config.isFirstToWin();
        boolean anyPlayerFoundBlock = teams.stream().anyMatch(
                team -> team.getPlayers().stream().anyMatch(
                        Player::isFoundBlock));
        boolean anyPlayerInEachTeamFoundBlock = teams.stream().allMatch(
                team -> team.getPlayers().stream().anyMatch(
                        Player::isFoundBlock));
        boolean anyTeamFoundBlock = teams.stream().anyMatch(
                team -> team.getPlayers().stream().allMatch(
                        Player::isFoundBlock));
        boolean allTeamsFoundBlock = teams.stream().allMatch(
                team -> team.getPlayers().stream().anyMatch(
                        Player::isFoundBlock));
        boolean allPlayersFoundBlock = teams.stream().allMatch(
                team -> team.getPlayers().stream().allMatch(
                        Player::isFoundBlock));

        // TODO: fix
        if (allPlayersRequiredForTeamWinCondition) {
            if (firstToWinCondition && anyTeamFoundBlock) {
                return true;
            } else if (!firstToWinCondition && allTeamsFoundBlock) {
                return true;
            }
        } else {
            if (firstToWinCondition && anyPlayerFoundBlock) {
                return true;
            } else if (!firstToWinCondition && anyPlayerInEachTeamFoundBlock) {
                return true;
            }
        }
        return allPlayersFoundBlock;
    }
}
