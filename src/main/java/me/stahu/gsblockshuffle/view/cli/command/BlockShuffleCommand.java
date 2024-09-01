package me.stahu.gsblockshuffle.view.cli.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.cli.command.subcommands.DebugSubcommand;
import me.stahu.gsblockshuffle.view.cli.command.subcommands.SettingsSubcommand;
import me.stahu.gsblockshuffle.view.cli.command.subcommands.TeamSubcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class BlockShuffleCommand extends Command implements CommandExecutor, TabCompleter {
    private final GameController gameController;
    private final DebugSubcommand debugSubcommand;
    private final TeamSubcommand teamSubcommand;
    private final SettingsSubcommand settingsSubcommand;

    private final MessageController messageController;
    private final MessageBuilder messageBuilder;

    private final Set<Player> players;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        if (command.getName().equals("blockshuffle")) {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage("This command has to be executed by a player.");
                return true;
            }
            Player player = getPlayer(sender);

            if (args.length == 0) {
                //check if player has permission to open the main menu
                if (!player.hasPermission("blockshuffle.admin")) {
                    messageController.commandResponse(player, messageBuilder.buildNoPermissionMessage(), false);
                    return true;
                }
                // implement gui
                return true;
            }
            if (!player.hasPermission("BlockShuffle.command." + args[0])) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                messageController.commandResponse(player, messageBuilder.buildNoPermissionMessage(), false);
                return true;
            }
            //execute subcommand
            switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseSubcommand(player, args);
                case "end" -> endGame(player);
                case "start" -> startGame(player);
                case "team" -> teamSubcommand.parseSubcommand(player, args);
                case "tp" -> parseTp(player, args);
                case "tpaccept" -> teamSubcommand.teamTeleportAccept(player);
                case "settings" -> settingsSubcommand.parseSubcommand(player, args);
                default -> player.sendMessage(ChatColor.RED + "Unknown BlockShuffle command.");
            }
        }
        return true;
    }

    private Player getPlayer(@NotNull CommandSender sender) {
        return players.stream()
                .filter(p -> p.getName().equals(sender.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Player not found."));
    }

    private void startGame(Player player) {
        try {
            gameController.startGame();
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    private void endGame(Player player) {
        try {
            gameController.endGame();
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    /**
     * This method is used to parse the teleport command by masking it as "/gsbs team tp"
     * and return the tab completions for it.
     *
     * @param sender The sender of the command, usually a player.
     * @param args   The arguments provided with the command.
     */
    private void parseTp(Player sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to teleport to.");
            return;
        }

        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "team";
        newArgs[1] = args[0];
        newArgs[2] = args[1];
        teamSubcommand.parseSubcommand(sender, newArgs);
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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            final List<String> suggestions = new LinkedList<>();
            commandsToCheck.forEach(cmd -> {
                if (sender.hasPermission("blockshuffle.command." + cmd)) {
                    suggestions.add(cmd);
                }
            });

            return filterCompletions(suggestions, args[0].toLowerCase());
        } else {
            Player player = getPlayer(sender);
            return switch (args[0].toLowerCase()) {
                case "debug" -> debugSubcommand.parseTabCompletions(player, args);
                case "settings" -> settingsSubcommand.parseTabCompletions(player, args);
                case "team" -> teamSubcommand.parseTabCompletions(player, args);
                case "tp" -> filterCompletions(playerList(), args[args.length - 1]);
                default -> Collections.emptyList();
            };
        }
    }
}