package me.stahu.gsblockshuffle.event.handler.team;

import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.event.type.team.TeamFailReason;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class TeamFailHandler {
    MessageController messageController;
    MessageBuilder messageBuilder;

    public void sendTeamFailMessages(Player player, TeamFailReason reason) {
        TextComponent message = switch (reason) {
            case ALREADY_IN_TEAM -> messageBuilder.buildAlreadyInTeamMessage(player);
            case NO_TEAM -> messageBuilder.buildNoTeamMessage(player);
            case NOT_A_LEADER -> messageBuilder.buildIsNotLeaderMessage(player);
            case NO_SUCH_REQUEST -> messageBuilder.buildNoSuchRequestMessage(player);
            case NO_SUCH_PLAYER -> messageBuilder.buildNoSuchPlayerMessage(player);
        };
        messageController.sendMessage(player, message);
    }
}
