package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.List;

public class SettingsSubcommand extends CommandBase implements Subcommand {
    private final GSBlockShuffle plugin;
    private final YamlConfiguration settings;
    public SettingsSubcommand(GSBlockShuffle plugin, YamlConfiguration settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    @Override
    public void parseSubcommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blockshuffle.command.settings")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return;
        }

        if (args.length == 1) {
            return;
        }

        switch (args[1].toLowerCase()) {
            case "save" -> plugin.saveConfiguration();
            case "load" -> plugin.loadConfiguration();
            case "preset" -> preset(sender, args);
        }
    }

    private void preset(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /blockshuffle settings preset <preset_name>");
            return;
        }

        String presetName = args[2];
        if (settings.getConfigurationSection("presets." + presetName) == null) {
            sender.sendMessage(ChatColor.RED + "Preset " + presetName + " does not exist.");
            return;
        }

        plugin.setPreset(presetName);
        sender.sendMessage(ChatColor.GREEN + "Preset " + presetName + " has been loaded.");
    }

    @Override
    public List<String> parseTabCompletions(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            // keep alphabetical order
            List<String> completions = List.of(
                    "load",
                    "preset",
                    "save"
            );
            return filterCompletions(completions, args[1]);
        }

        switch (args[1].toLowerCase()) {
            case "preset" -> {
                if (args.length == 3) {
                    return filterCompletions(plugin.getPresetNames(), args[2]);
                }
            }
        }

        return Collections.emptyList();
    }
}
