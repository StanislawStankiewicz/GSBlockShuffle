package me.stahu.gsblockshuffle.commands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.gui.page.GuiPage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class BlockShuffleCommand implements CommandExecutor {
    private final GuiPage categorySelectionGui;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;
    private final ArrayList<CommandSender> debugModeUsers = new ArrayList<>();

    public BlockShuffleCommand(GuiPage categorySelectionGui, GameStateManager gameStateManager, YamlConfiguration settings, GSBlockShuffle plugin) {
        this.categorySelectionGui = categorySelectionGui;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("Command received");
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
            if (args[0].equalsIgnoreCase("debug")) {
                onDebugCommand(sender, command, label, args);
            }
        }
        return true;
    }

    private void onDebugCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!debugModeUsers.contains(sender)) {
            sender.sendMessage(ChatColor.RED + "WARNING! Debug mode is not supported and may cause the plugin to behave non-deterministically.");
            debugModeUsers.add(sender);
        }
        if (args.length == 1) {
            for (String key : settings.getKeys(false)) {
                sender.sendMessage(ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
            }
            return;
        }
        if (args.length < 2) {
            return;
        }
        if (args[1].equalsIgnoreCase("saveSettings")) {
            try {
                plugin.saveSettings(settings);
                sender.sendMessage(ChatColor.GREEN + "Successfully saved settings.");
            } catch (Exception e) {
                System.out.println(e);
                sender.sendMessage(ChatColor.RED + "Failed to save settings.");
            }
            return;
        }
        if(args[1].equalsIgnoreCase("playTing")){
            sender.sendMessage(ChatColor.GREEN + "Playing ting sound.");
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.5F);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1F);
            return;
        }
        if (args[1].equalsIgnoreCase("startGame")) {
            sender.sendMessage(ChatColor.GREEN + "Game start invoked.");
            gameStateManager.setGameState(1);
            return;
        }
        if (args[1].equalsIgnoreCase("endGame")) {
            sender.sendMessage(ChatColor.GREEN + "Game end invoked");
            gameStateManager.setGameState(0);
            return;
        }
        if (args[1].equalsIgnoreCase("newRound")) {
            sender.sendMessage(ChatColor.GREEN + "New round invoked.");
            gameStateManager.newRound();
            return;
        }
        if (args[1].equalsIgnoreCase("endRound")) {
            sender.sendMessage(ChatColor.GREEN + "End round invoked.");
            gameStateManager.endRound();
            return;
        }
        if(args[1].equalsIgnoreCase("getRoundsRemaining")) {
            sender.sendMessage(ChatColor.GREEN + "Rounds remaining: " + ChatColor.DARK_AQUA + gameStateManager.getRoundsRemaining());
            return;
        }
        if (args[1].equalsIgnoreCase("get")) {
            String key = args[2];
            if (!settings.contains(key)) {
                sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
                return;
            }
            sender.sendMessage(ChatColor.DARK_AQUA + key + ChatColor.WHITE + ": " + ChatColor.DARK_GREEN + settings.get(key));
            return;
        }
        if (args[1].equalsIgnoreCase("set")) {
            String key = args[2];

            if (!settings.contains(key)) {
                sender.sendMessage(ChatColor.RED + "Key " + ChatColor.DARK_AQUA + key + ChatColor.RED + " does not exist.");
                return;
            }

            String value = args[3];

            plugin.changeSetting(settings, key, value);

            sender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.DARK_AQUA + key + " to " + ChatColor.DARK_GREEN + value);
            return;
        }
        // if none of the above commands were correct:
        sender.sendMessage(ChatColor.RED + "Invalid command.");
    }
}