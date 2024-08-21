package me.stahu.gsblockshuffle.event.handler.game;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.sound.SoundPlayer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RequiredArgsConstructor
public class GameEndHandler {

    final MessageController messageController;
    final SoundPlayer soundPlayer;
    final MessageBuilder messageBuilder;

    public void sendGameEndMessage() {
        messageController.sendMessageToAll(messageBuilder.buildEndGameMessage());
    }

    public void sendEndScoresMessage(Set<Team> teams) {
        messageController.sendMessageToAll(messageBuilder.buildEndScoresMessage(teams));
    }

    public void playWinnerSound(Set<Team> teams) {
        List<Team> winningTeams = teams.stream()
                .filter(team -> team.getScore() == teams.stream()
                        .mapToInt(Team::getScore)
                        .max()
                        .orElseThrow(NoSuchElementException::new))
                .toList();
        winningTeams.forEach(team ->
                team.getPlayers().forEach(soundPlayer::playWinnerSound)
        );
    }
}
