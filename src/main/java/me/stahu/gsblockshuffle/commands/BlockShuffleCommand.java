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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BlockShuffleCommand implements CommandExecutor {
    private final GuiPage categorySelectionGui;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;
    private final TeamsManager teamManager;
    private final DebugSubcommand debugSubcommand;
    private final TeamSubcommand teamSubcommand;

    public BlockShuffleCommand(GuiPage categorySelectionGui, GameStateManager gameStateManager, YamlConfiguration settings, GSBlockShuffle plugin, TeamsManager teamManager) {
        this.categorySelectionGui = categorySelectionGui;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.debugSubcommand = new DebugSubcommand(plugin, gameStateManager, settings);
        this.teamSubcommand = new TeamSubcommand(plugin, gameStateManager, settings, teamManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("gsblockshuffle")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("you cannot execute this command from console.");
                return true;
            }
            if (!player.hasPermission("gsblockshuffle.admin")) {
                player.sendMessage(ChatColor.RED + "you do not have permission to execute this command.");
                return true;
            }
            if (args.length == 0) {
                this.categorySelectionGui.open(player);
            }
            switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseSubcommand(sender, command, label, args);
                case "team" -> teamSubcommand.parseSubcommand(sender, command, label, args);
                case "start" -> startGame(player);
                case "end" -> endGame(player);
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
}