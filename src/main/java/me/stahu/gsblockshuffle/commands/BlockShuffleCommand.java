package me.stahu.gsblockshuffle.commands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.subcommands.DebugSubcommand;
import me.stahu.gsblockshuffle.commands.subcommands.SettingsSubcommand;
import me.stahu.gsblockshuffle.commands.subcommands.TeamSubcommand;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.gui.page.GuiPage;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlockShuffleCommand extends CommandBase implements CommandExecutor, TabCompleter {
    private final GuiPage mainMenuGui;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;
    private final TeamsManager teamManager;
    private final DebugSubcommand debugSubcommand;
    private final TeamSubcommand teamSubcommand;
    private final SettingsSubcommand settingsSubcommand;

    public BlockShuffleCommand(GuiPage mainMenuGui, GameStateManager gameStateManager, YamlConfiguration settings, GSBlockShuffle plugin, TeamsManager teamManager) {
        this.mainMenuGui = mainMenuGui;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.debugSubcommand = new DebugSubcommand(plugin, gameStateManager, teamManager, settings);
        this.teamSubcommand = new TeamSubcommand(plugin, gameStateManager, settings, teamManager);
        this.settingsSubcommand = new SettingsSubcommand(plugin, settings);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("blockshuffle")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You cannot execute this command from console.");
                return true;
            }

            if (args.length == 0) {
                //check if player has permission to open the main menu
                if (!player.hasPermission("blockshuffle.admin")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                    return true;
                }
                this.mainMenuGui.open(player);
                return true;
            }

            //check if player has permission to execute the subcommand
            if (!sender.hasPermission("BlockShuffle.command." + args[0].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }
            //execute subcommand
            switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseSubcommand(sender, command, label, args);
                case "end" -> endGame(player);
                case "start" -> startGame(player);
                case "team" -> teamSubcommand.parseSubcommand(sender, command, label, args);
                case "tp" -> parseTp(sender, args);
                case "tpaccept" -> teamSubcommand.teamTeleportAccept(player);
                case "settings" -> settingsSubcommand.parseSubcommand(sender, command, label, args);
                default -> sender.sendMessage(ChatColor.RED + "Unknown BlockShuffle command.");
            }
        }
        return true;
    }

    // /gsbs end
    private void endGame(Player player) {
        if (!gameStateManager.setGameState(0)) {
            player.sendMessage(ChatColor.RED + "Game is already stopped.");
        }
    }

    // /gsbs start
    private void startGame(Player player) {
        if (!gameStateManager.setGameState(1)) {
            player.sendMessage(ChatColor.RED + "Game is already running.");
        }
    }

    // /gsbs tp <team> <player>
    private void teamTeleportRequest(CommandSender sender, String[] args) {
        // insert "team" subcommand to ensure correct indexes
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "team";
        newArgs[1] = args[0];
        newArgs[2] = args[1];
        System.out.println("newArgs: " + newArgs[0] + " " + newArgs[1] + " " + newArgs[2]);
        teamSubcommand.teamTeleportRequest(sender, newArgs);
    }

    private final List<String> commandsToCheck = List.of(
            "debug",
            "end",
            "settings",
            "start",
            "team",
            "tp",
            "tpaccept");
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            final List<String> suggestions = new LinkedList<>();

            commandsToCheck.forEach(cmd -> {
                if (sender.hasPermission("GSBlockShuffle.command." + cmd)) {
                    suggestions.add(cmd);
                }
            });

            return filterCompletions(suggestions, args[0]);
        } else {
            return switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseTabCompletions(sender, command, label, args);
                case "settings" -> settingsSubcommand.parseTabCompletions(sender, command, label, args);
                case "team" -> teamSubcommand.parseTabCompletions(sender, command, label, args);
                case "tp" -> filterCompletions(playerList(), args[args.length - 1]);
                default -> Collections.emptyList();
            };
        }
    }

    /**
     * This method is used to parse the teleport command by masking it as "/gsbs team tp"
     * and return the tab completions for it.
     *
     * @param sender The sender of the command, usually a player.
     * @param args   The arguments provided with the command.
     * @return A list of strings representing the possible completions for the teleport command.
     */
    private List<String> parseTp(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "team";
        newArgs[1] = args[0];
        return teamSubcommand.parseTabCompletions(sender, null, null, newArgs);
    }
}