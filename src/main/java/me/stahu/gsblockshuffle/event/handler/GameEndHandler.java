package me.stahu.gsblockshuffle.event.handler;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;

import java.util.Set;

@RequiredArgsConstructor
public class GameEndHandler {

    final MessageController messageController;
    final MessageBuilder messageBuilder;

    public void sendGameEndMessage() {
        messageController.sendMessageToAll(messageBuilder.buildEndGameMessage());
    }

    public void sendEndScoresMessage(Set<Team> teams) {
        messageController.sendMessageToAll(messageBuilder.buildEndScoresMessage(teams));
    }
}
