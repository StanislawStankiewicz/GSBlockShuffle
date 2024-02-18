package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamSubcommand {
    private final GSBlockShuffle plugin;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final TeamsManager teamManager;

    public TeamSubcommand(GSBlockShuffle plugin, GameStateManager gameStateManager, YamlConfiguration settings, TeamsManager teamManager) {
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
        this.teamManager = teamManager;
    }

    public void parseSubcommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gsblockshuffle.command.team")) {
            sender.sendMessage("You do not have permission to execute this command.");
            return;
        }
        if (args.length == 1) {
            sender.sendMessage("Usage: /gsblockshuffle team <subcommand> [args]");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "create" -> createTeam(sender, args);
            case "join" -> joinTeam(sender, args);
            case "accept" -> teamRequestAccept(sender, args);
            case "tp" -> teamTeleportRequest(sender, args);
            case "tpaccept" -> teamTeleportAccept(sender);
        }
    }

    private void createTeam(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team name.");
            return;
        }
        if (teamManager.getTeam(args[2]) != null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " already exists.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.GREEN + " has been created.");
        teamManager.addTeam(args[2], ChatColor.WHITE);
        teamManager.addPlayerToTeam((Player) sender, teamManager.getTeam(args[2]), true);
    }

    private void joinTeam(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team to join.");
            return;
        }
        Team team = teamManager.getTeam(args[2]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " does not exist.");
            return;
        }
        teamManager.addPlayerToTeamRequest((Player) sender, team);
    }

    private void teamRequestAccept(CommandSender sender, String[] args) {
        if (!teamManager.teamRequestAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending team requests.");
        }
    }

    private void teamTeleportRequest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to teleport to.");
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " is not online.");
            return;
        }
        if (!teamManager.teamTeleportRequest((Player) sender, target)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to a player on a different team.");
        }
    }

    private void teamTeleportAccept(CommandSender sender) {
        if(!teamManager.teamTeleportAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending teleport requests.");
        }
    }
}
