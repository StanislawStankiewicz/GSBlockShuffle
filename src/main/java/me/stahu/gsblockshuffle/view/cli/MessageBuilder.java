package me.stahu.gsblockshuffle.view.cli;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.message.GameMessageBuilder;
import me.stahu.gsblockshuffle.view.cli.message.TeamMessageBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Set;

@RequiredArgsConstructor
public class MessageBuilder {

    final GameMessageBuilder gameMessageBuilder;
    final TeamMessageBuilder teamMessageBuilder;

    // Game-related messages
    public TextComponent buildBlockAssignmentMessage(Block block) {
        return gameMessageBuilder.buildBlockAssignmentMessage(block);
    }

    public TextComponent buildGameStartMessage() {
        return gameMessageBuilder.buildGameStartMessage();
    }

    public TextComponent buildEndGameMessage() {
        return gameMessageBuilder.buildEndGameMessage();
    }

    public TextComponent buildEndScoresMessage(Set<Team> teams) {
        return gameMessageBuilder.buildEndScoresMessage(teams);
    }

    public TextComponent buildBlockFoundMessage(Player player, Block block) {
        return gameMessageBuilder.buildBlockFoundMessage(player, block);
    }

    // Team-related messages
    public TextComponent buildCreateTeamMessage(Team team) {
        return teamMessageBuilder.buildCreateTeamMessage(team);
    }

    public TextComponent buildRemoveTeamMessage(Team team) {
        return teamMessageBuilder.buildRemoveTeamMessage(team);
    }

    public TextComponent buildLeaveTeamMessage(Team team) {
        return teamMessageBuilder.buildLeaveTeamMessage(team);
    }

    public TextComponent buildAddPlayerToTeamSenderMessage(Team team, Player player) {
        return teamMessageBuilder.buildAddPlayerToTeamSenderMessage(team, player);
    }

    public TextComponent buildAddPlayerToTeamReceiverMessage(Team team, Player player) {
        return teamMessageBuilder.buildAddPlayerToTeamReceiverMessage(team, player);
    }

    public TextComponent buildInvitePlayerToTeamSenderMessage(Team team, Player player) {
        return teamMessageBuilder.buildInvitePlayerToTeamSenderMessage(team, player);
    }

    public TextComponent buildInvitePlayerToTeamReceiverMessage(Team team, Player player) {
        return teamMessageBuilder.buildInvitePlayerToTeamReceiverMessage(team, player);
    }

    public TextComponent buildAcceptInviteSenderMessage(Team team) {
        return teamMessageBuilder.buildAcceptInviteSenderMessage(team);
    }

    public TextComponent buildAcceptInviteReceiverMessage(Team team, Player player) {
        return teamMessageBuilder.buildAcceptInviteReceiverMessage(team, player);
    }

    public TextComponent buildRequestToJoinTeamSenderMessage(Team team, Player player) {
        return teamMessageBuilder.buildRequestToJoinTeamSenderMessage(team, player);
    }

    public TextComponent buildRequestToJoinTeamReceiverMessage(Team team, Player player) {
        return teamMessageBuilder.buildRequestToJoinTeamReceiverMessage(team, player);
    }

    public TextComponent buildAcceptRequestSenderMessage(Team team, Player player) {
        return teamMessageBuilder.buildAcceptRequestSenderMessage(team, player);
    }

    public TextComponent buildAcceptRequestReceiverMessage(Team team, Player player) {
        return teamMessageBuilder.buildAcceptRequestReceiverMessage(team, player);
    }

    public TextComponent buildKickFromTeamSenderMessage(Team team, Player player) {
        return teamMessageBuilder.buildKickFromTeamSenderMessage(team, player);
    }

    public TextComponent buildKickFromTeamReceiverMessage(Team team) {
        return teamMessageBuilder.buildKickFromTeamReceiverMessage(team);
    }

    public TextComponent buildNoTeamMessage(Player player) {
        return teamMessageBuilder.buildNoTeamMessage(player);
    }

    public TextComponent buildAlreadyInTeamMessage(Player player) {
        return teamMessageBuilder.buildAlreadyInTeamMessage(player);
    }

    public TextComponent buildIsNotLeaderMessage(Player player) {
        return teamMessageBuilder.buildIsNotLeaderMessage(player);
    }

    public TextComponent buildNoSuchRequestMessage(Player player) {
        return teamMessageBuilder.buildNoSuchRequestMessage(player);
    }

    public TextComponent buildNoSuchPlayerMessage(Player player) {
        return teamMessageBuilder.buildNoSuchPlayerMessage(player);
    }
}
