package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.event.GameStateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;

public class DebugSubcommand {
    private final GSBlockShuffle plugin;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final HashSet<CommandSender> debugModeUsers = new HashSet<>();

    public DebugSubcommand(GSBlockShuffle plugin, GameStateManager gameStateManager, YamlConfiguration settings) {
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
    }

    public void parseSubcommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gsblockshuffle.command.debug")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return;
        }

        if (!debugModeUsers.contains(sender)) {
            debugModeUsers.add(sender);
            sender.sendMessage(ChatColor.RED + "WARNING! Debug mode may cause the plugin to behave non-deterministically.");
        }

        if (args.length == 1) {
            showOptions(sender);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "startgame" -> startGame(sender);
            case "newround" -> newRound(sender);
            case "endround" -> endRound(sender);
            case "endgame" -> endGame(sender);
            case "getroundsremaining" -> getRoundsRemaining(sender);
            case "get" -> getSetting(sender, args);
            case "set" -> setSetting(sender, args);
            case "savesettings" -> saveSettings(sender);
        }
    }

    private void showOptions(CommandSender sender) {
        for (String key : settings.getKeys(false)) {
            sender.sendMessage(ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
        }
    }

    private void startGame(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Game start invoked.");
        gameStateManager.setGameState(1);
    }

    private void newRound(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "New round invoked.");
        gameStateManager.newRound();
    }

    private void endRound(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "End round invoked.");
        gameStateManager.endRound();
    }

    private void endGame(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "End game invoked.");
        gameStateManager.setGameState(0);
    }

    private void getRoundsRemaining(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Rounds remaining: " + ChatColor.DARK_AQUA + gameStateManager.getRoundsRemaining());
    }

    private void getSetting(CommandSender sender, String[] args) {
        String key = args[2];
        if (!settings.contains(key)) {
            sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
            return;
        }
        sender.sendMessage(ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
    }

    private void setSetting(CommandSender sender, String[] args) {
        String key = args[2];

        if (!settings.contains(key)) {
            sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
            return;
        }

        String value = args[3];

        plugin.changeSetting(settings, key, value);

        sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.DARK_AQUA + key + " to " + ChatColor.DARK_GREEN + value);
    }

    private void saveSettings(CommandSender sender) {
        try {
            plugin.saveConfiguration();
            sender.sendMessage(ChatColor.GREEN + "Successfully saved settings.");
        } catch (Exception e) {
            System.out.println(e);
            sender.sendMessage(ChatColor.RED + "Failed to save settings.");
        }
    }
}
