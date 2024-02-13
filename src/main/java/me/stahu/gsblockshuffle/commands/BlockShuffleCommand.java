package me.stahu.gsblockshuffle.commands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.page.GuiPage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockShuffleCommand implements CommandExecutor {
    private final GuiPage categorySelectionGui;

    public BlockShuffleCommand(GuiPage categorySelectionGui) {
        this.categorySelectionGui = categorySelectionGui;
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
        return true;
    }
}