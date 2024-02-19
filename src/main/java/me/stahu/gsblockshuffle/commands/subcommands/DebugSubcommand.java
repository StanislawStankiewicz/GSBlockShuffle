package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.CommandBase;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DebugSubcommand extends CommandBase implements Subcommand {
    private final GSBlockShuffle plugin;
    private final GameStateManager gameStateManager;
    private final TeamsManager teamsManager;
    private final YamlConfiguration settings;
    private final HashSet<CommandSender> debugModeUsers = new HashSet<>();

    public DebugSubcommand(GSBlockShuffle plugin, GameStateManager gameStateManager, TeamsManager teamsManager, YamlConfiguration settings) {
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
        this.teamsManager = teamsManager;
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
            case "tp" -> teleportToPlayer(sender, args);
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

    private void teleportToPlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to teleport to.");
            return;
        }

        String playerName = args[2];
        if (plugin.getServer().getPlayer(playerName) == null) {
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_AQUA + playerName + ChatColor.RED + " is not online.");
            return;
        }

        if (!(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to teleport.");
            return;
        }

        Player target = plugin.getServer().getPlayer(playerName);
        teamsManager.teamTeleportRequest((Player) sender, target);
        teamsManager.teamTeleportAccept(target);
    }

    @Override
    public List<String> parseTabCompletions(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            // keep this in alphabetical order
            List<String> options = List.of(
                    "endGame",
                    "endRound",
                    "get",
                    "getRoundsRemaining",
                    "newRound",
                    "saveSettings",
                    "set",
                    "startGame",
                    "tp");
            return filterCompletions(options, args[1]);
        }

        List<String> settingKeysList = settings.getKeys(false).stream().toList();
        switch (args[1].toLowerCase()) {
            case "get" -> {
                return filterCompletions(settingKeysList, args[2]);
            }
            case "set" -> {
                if (args.length == 3) {
                    return filterCompletions(settingKeysList, args[2]);
                }
            }
        }

        return Collections.emptyList();
    }
}
