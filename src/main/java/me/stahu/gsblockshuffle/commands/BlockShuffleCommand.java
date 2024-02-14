package me.stahu.gsblockshuffle.commands;

import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.gui.page.GuiPage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class BlockShuffleCommand implements CommandExecutor {
    private final GuiPage categorySelectionGui;
    private final GameStateManager gameStateManager;

    public BlockShuffleCommand(GuiPage categorySelectionGui, GameStateManager gameStateManager) {
        this.categorySelectionGui = categorySelectionGui;
        this.gameStateManager = gameStateManager;
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
        }
        if (command.getName().equals("gsblockshuffle_set_game_state")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("you cannot execute this command from console.");
                return true;
            }
            if (!player.hasPermission("gsblockshuffle.admin")) {
                player.sendMessage(ChatColor.RED + "you do not have permission to execute this command.");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "You must specify a game state.");
                return true;
            }
            if (args[0].equals("solo")) {
                gameStateManager.setGameState(1);
                player.sendMessage(ChatColor.GREEN + "Game state set to solo.");
            }
        }
        return true;
    }
}