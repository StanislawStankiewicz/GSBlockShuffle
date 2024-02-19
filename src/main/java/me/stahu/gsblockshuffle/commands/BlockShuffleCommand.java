package me.stahu.gsblockshuffle.commands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.subcommands.DebugSubcommand;
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

    public BlockShuffleCommand(GuiPage mainMenuGui, GameStateManager gameStateManager, YamlConfiguration settings, GSBlockShuffle plugin, TeamsManager teamManager) {
        this.mainMenuGui = mainMenuGui;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.debugSubcommand = new DebugSubcommand(plugin, gameStateManager, teamManager, settings);
        this.teamSubcommand = new TeamSubcommand(plugin, gameStateManager, settings, teamManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("gsblockshuffle")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You cannot execute this command from console.");
                return true;
            }
            if (!player.hasPermission("gsblockshuffle.admin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }
            if (args.length == 0) {
                this.mainMenuGui.open(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseSubcommand(sender, command, label, args);
                case "team" -> teamSubcommand.parseSubcommand(sender, command, label, args);
                case "tp" -> teamSubcommand.teamTeleportRequest(sender, args);
                case "tpaccept" -> teamSubcommand.teamTeleportAccept(player);
                case "start" -> startGame(player);
                case "end" -> endGame(player);
                default -> sender.sendMessage(ChatColor.RED + "Unknown command.");
            }
        }
        return true;
    }

    private void startGame(Player player) {
        if (!gameStateManager.setGameState(1)) {
            player.sendMessage(ChatColor.RED + "Game is already running.");
        }
    }

    private void endGame(Player player) {
        if (!gameStateManager.setGameState(0)) {
            player.sendMessage(ChatColor.RED + "Game is already stopped.");
        }
    }

    private final List<String> commandsToCheck = List.of("debug", "team", "start", "end");

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
                case "team" -> teamSubcommand.parseTabCompletions(sender, command, label, args);
                // the missing subcommands don't have tab completions, don't bother including them.
                default -> Collections.emptyList();
            };
        }
    }
}