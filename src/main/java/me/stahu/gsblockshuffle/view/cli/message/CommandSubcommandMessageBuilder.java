package me.stahu.gsblockshuffle.view.cli.message;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class CommandSubcommandMessageBuilder {

    final LocalizationManager localizationManager;

    public TextComponent buildSpecifyTeamNameMessage() {
        return new TextComponent(localizationManager.getMessage("specify_team_name"));
    }

    public TextComponent buildSpecifyColorMessage() {
        return new TextComponent(localizationManager.getMessage("specify_color"));
    }

    public TextComponent buildInvalidColorMessage() {
        return new TextComponent(localizationManager.getMessage("invalid_color"));
    }

    public TextComponent buildSpecifyPlayerMessage() {
        return new TextComponent(localizationManager.getMessage("specify_player"));
    }

    public TextComponent buildNoTeamMessage(Player sender) {
        return new TextComponent(String.format(localizationManager.getMessage("no_team"), sender.getName()));
    }

    public TextComponent buildAlreadyInTeamMessage(Player sender) {
        return new TextComponent(String.format(localizationManager.getMessage("already_in_team"), sender.getName()));
    }

    public TextComponent buildNoSuchInviteMessage(Player sender) {
        return new TextComponent(String.format(localizationManager.getMessage("no_such_invite"), sender.getName()));
    }

    public TextComponent buildNoSuchRequestMessage(Player sender) {
        return templatePlayerMessage(sender, "no_such_request");
    }

    public TextComponent buildNotLeaderMessage(Player sender) {
        return templatePlayerMessage(sender, "not_leader");
    }

    public TextComponent buildNoSuchTeamMessage(String arg) {
        return new TextComponent(
                String.format(localizationManager.getMessage("no_such_team"), ChatColor.AQUA + arg + ChatColor.RED));
    }

    public TextComponent buildNoPermissionMessage() {
        return new TextComponent(localizationManager.getMessage("no_permission"));
    }

    public TextComponent buildNoSuchPlayerMessage(String playerName) {
        return new TextComponent(
                String.format(localizationManager.getMessage("no_such_player"), ChatColor.AQUA + playerName + ChatColor.RED));
    }

    private TextComponent templatePlayerMessage(Player player, String key) {
        String template = localizationManager.getMessage(key);
        String message = String.format(template, ChatColor.AQUA + player.getName() + ChatColor.RED);
        return new TextComponent(message);
    }
}
