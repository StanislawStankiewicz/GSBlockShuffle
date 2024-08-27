package me.stahu.gsblockshuffle.view.cli.message;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

@RequiredArgsConstructor
public class SettingsSubcommandMessageBuilder {

    private final LocalizationManager localizationManager;

    public TextComponent buildSettingsMessage(Config config) {
        YamlConfiguration settings = config.getSettings();
        TextComponent message = new TextComponent(ChatColor.AQUA + localizationManager.getMessage("settings"));
        for (String key : settings.getKeys(false)) {
            message.addExtra("\n" + ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
        }
        return message;
    }
}
