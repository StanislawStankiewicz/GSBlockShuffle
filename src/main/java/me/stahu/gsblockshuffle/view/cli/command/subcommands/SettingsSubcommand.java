package me.stahu.gsblockshuffle.view.cli.command.subcommands;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.cli.command.Command;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class SettingsSubcommand extends Command implements Subcommand {
    private final Config config;
    final MessageController messageController;
    final MessageBuilder messageBuilder;

    @Override
    public void parseSubcommand(Player sender, String[] args) {
        if (args.length == 1) {
            TextComponent m = messageBuilder.buildSettingsMessage(config);
            messageController.commandResponse(sender, m, false);
            return;
        }

        if (!sender.hasPermission("blockshuffle.command.settings")) {
            messageController.commandResponse(sender, messageBuilder.buildNoPermissionMessage(), false);
            return;
        }
        // TODO implement
//        switch (args[1].toLowerCase()) {
//            case "save" -> plugin.saveConfiguration();
//            case "load" -> plugin.loadConfiguration();
//            case "preset" -> preset(sender, args);
//        }
    }

    private void preset(Player sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /blockshuffle settings preset <preset_name>");
            return;
        }

        // TODO implement
//        String presetName = args[2];
//        if (!plugin.getPresetNames().contains(presetName)) {
//            sender.sendMessage(ChatColor.RED + "Preset " + presetName + " does not exist.");
//            return;
//        }
//
//        plugin.setPreset(presetName);
//        sender.sendMessage(ChatColor.GREEN + "Preset " + presetName + " has been loaded.");
    }

    @Override
    public List<String> parseTabCompletions(Player sender, String[] args) {
        if (args.length == 2) {
            // keep alphabetical order
            List<String> completions = List.of(
                    "load",
                    "preset",
                    "save"
            );
            return filterCompletions(completions, args[1]);
        }

        if (args[1].equalsIgnoreCase("preset")) {
            if (args.length == 3) {
                // TODO implement
//                return filterCompletions(plugin.getPresetNames(), args[2]);
            }
        }

        return Collections.emptyList();
    }
}
