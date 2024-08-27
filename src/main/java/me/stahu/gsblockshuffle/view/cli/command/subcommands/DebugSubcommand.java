package me.stahu.gsblockshuffle.view.cli.command.subcommands;

import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.controller.GameState;
import me.stahu.gsblockshuffle.manager.PlayerManager;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebugSubcommand extends Command implements Subcommand {
    private final GameController gameController;
    private final PlayerManager playerManager;
    private final YamlConfiguration settings;
    private final Set<Player> debugModeUsers = new HashSet<>();

    public DebugSubcommand(GameController gameController, PlayerManager playerManager, YamlConfiguration settings) {
        this.gameController = gameController;
        this.playerManager = playerManager;
        this.settings = settings;
    }

    public void parseSubcommand(Player sender, String[] args) {
        if (!sender.hasPermission("blockshuffle.command.debug")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return;
        }

        if (!debugModeUsers.contains(sender)) {
            debugModeUsers.add(sender);
            sender.sendMessage(ChatColor.RED + "WARNING! Debug mode may cause the plugin to behave non-deterministically.");
        }

        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "You must specify a command.");
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
            // default case error
        }
    }

    private void startGame(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Game start invoked.");
        gameController.setGameState(GameState.GAME_START);
    }

    private void newRound(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "New round invoked.");
        gameController.setGameState(GameState.ROUND_NEW);
    }

    private void endRound(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "End round invoked.");
        gameController.setGameState(GameState.ROUND_END);
    }

    private void endGame(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "End game invoked.");
        gameController.setGameState(GameState.GAME_END);
    }

    private void getRoundsRemaining(Player sender) {
        // TODO implement
//        sender.sendMessage(ChatColor.GREEN + "Rounds remaining: " + ChatColor.DARK_AQUA + gameController.getRoundsRemaining());
    }

    private void getSetting(Player sender, String[] args) {
        String key = args[2];
        if (!settings.contains(key)) {
            sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
            return;
        }
        sender.sendMessage(ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
    }

    private void setSetting(Player sender, String[] args) {
        String key = args[2];

        if (!settings.contains(key)) {
            sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
            return;
        }

        String value = args[3];
        // TODO implement
//        plugin.changeSetting(settings, key, value);

        sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.DARK_AQUA + key + ChatColor.GREEN + " to " + ChatColor.DARK_GREEN + value);
    }

    private void teleportToPlayer(Player sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to teleport to.");
            return;
        }
        String playerName = args[2];
        Player target = playerManager.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst().orElse(null);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_AQUA + playerName + ChatColor.RED + " is not online.");
            return;
        }
        // TODO implement
//        playerManager.teamTeleportRequest((Player) sender, target);
//        playerManager.teamTeleportAccept(target);
    }

    @Override
    public List<String> parseTabCompletions(Player sender, String[] args) {
        if (args.length == 2) {
            // keep alphabetical order
            List<String> completions = List.of(
                    "endGame",
                    "endRound",
                    "get",
                    "getRoundsRemaining",
                    "newRound",
                    "saveSettings",
                    "set",
                    "startGame",
                    "tp");
            return filterCompletions(completions, args[1]);
        }
        List<String> settingKeysList = settings.getKeys(false).stream().toList();
        switch (args[1].toLowerCase()) {
            case "get", "set" -> {
                if (args.length == 3) {
                    return filterCompletions(settingKeysList, args[2]);
                }
            }
        }
        return Collections.emptyList();
    }
}
