package me.stahu.gsblockshuffle.view.cli.message;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import net.md_5.bungee.api.chat.TextComponent;

@RequiredArgsConstructor
public class TeamMessageBuilder {

    final LocalizationManager localizationManager;

    public TextComponent buildCreateTeamMessage(Team team) {
        return templateTeamMessage(team, "create_team");
    }

    public TextComponent buildRemoveTeamMessage(Team team) {
        return templateTeamMessage(team, "remove_team");
    }

    public TextComponent buildLeaveTeamMessage(Team team) {
        return templateTeamMessage(team, "leave_team");
    }

    public TextComponent buildAddPlayerToTeamSenderMessage(Team team, Player player) {
        return templatePlayerTeamMessage(team, player, "add_player_to_team_sender");
    }

    public TextComponent buildAddPlayerToTeamReceiverMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "add_player_to_team_receiver");
    }

    public TextComponent buildInvitePlayerToTeamSenderMessage(Team team, Player player) {
        return templatePlayerTeamMessage(team, player, "invite_player_to_team_sender");
    }

    public TextComponent buildInvitePlayerToTeamReceiverMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "invite_player_to_team_receiver");
    }

    public TextComponent buildAcceptInviteSenderMessage(Team team) {
        return templateTeamMessage(team, "accept_invite_sender");
    }

    public TextComponent buildAcceptInviteReceiverMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "accept_invite_receiver");
    }

    public TextComponent buildRequestToJoinTeamSenderMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "request_to_join_team_sender");
    }

    public TextComponent buildRequestToJoinTeamReceiverMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "request_to_join_team_receiver");
    }

    public TextComponent buildAcceptRequestSenderMessage(Team team, Player player) {
        return templatePlayerTeamMessage(team, player, "accept_request_sender");
    }

    public TextComponent buildAcceptRequestReceiverMessage(Team team, Player player) {
        return templateTeamPlayerMessage(team, player, "accept_request_receiver");
    }

    public TextComponent buildKickFromTeamSenderMessage(Team team, Player player) {
        return templatePlayerTeamMessage(team, player, "kick_from_team_sender");
    }

    public TextComponent buildKickFromTeamReceiverMessage(Team team) {
        return templateTeamMessage(team, "kick_from_team_receiver");
    }

    private TextComponent templateTeamMessage(Team team, String templateId) {
        String template = localizationManager.getMessage(templateId);
        String message = String.format(template, team.getName());
        return new TextComponent(message);
    }

    private TextComponent templateTeamPlayerMessage(Team team, Player player, String templateId) {
        String template = localizationManager.getMessage(templateId);
        String message = String.format(template, player.getName(), team.getName());
        return new TextComponent(message);
    }

    private TextComponent templatePlayerTeamMessage(Team team, Player player, String templateId) {
        String template = localizationManager.getMessage(templateId);
        String message = String.format(template, team.getName(), player.getName());
        return new TextComponent(message);
    }
}
