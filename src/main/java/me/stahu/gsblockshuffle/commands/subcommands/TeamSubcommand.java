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

import java.util.Collections;
import java.util.List;

public class TeamSubcommand implements Subcommand {
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
        // tell player his team and team members
        if (args.length == 1) {
            Team team = teamManager.getTeam((Player) sender);
            if (team == null) {
                sender.sendMessage(ChatColor.RED + "You are not on a team.");
                return;
            }
            sender.sendMessage(ChatColor.GRAY + "Team " + team.getDisplayName() + ChatColor.GRAY + " members:");
            for (String entry : team.getEntries()) {
                String message = " ● ";
                if (teamManager.teamCaptains.containsKey(Bukkit.getPlayer(entry))) {
                    message += ChatColor.AQUA + entry;
                } else {
                    message += entry;
                }
                sender.sendMessage(message);
            }
            return;
        }
        switch (args[1].toLowerCase()) {
            case "create" -> createTeam(sender, args);
            case "join" -> joinTeamRequest(sender, args);
            case "invite" -> teamInviteRequest(sender, args);
            case "leave" -> leaveTeam(sender);
            case "accept" -> joinTeamAccept(sender, args);
        }
    }

    private void createTeam(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team name.");
            return;
        }
        // check if team already exists
        Team team = teamManager.getTeam(args[2]);

        if (teamManager.teams.contains(team)) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.RED + " already exists.");
            return;
        }
        team = teamManager.addTeam(args[2], ChatColor.WHITE);
        teamManager.addPlayerToTeam((Player) sender, teamManager.getTeam(args[2]), false);
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.GREEN + " has been created.");
    }

    private void joinTeamRequest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team to join.");
            return;
        }
        Team team = teamManager.getTeam(args[2]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " does not exist.");
            return;
        }
        teamManager.joinTeamRequest((Player) sender, team);
    }

    private void joinTeamAccept(CommandSender sender, String[] args) {
        if (teamManager.teamCaptains.containsKey((Player) sender)) {
            if (!teamManager.joinTeamRequestAccept((Player) sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have any pending team requests.");
                return;
            }
            return;
        }
        // accept invite
        // sender should have no team
        if (!teamManager.teamInviteRequestAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending team invites.");
        }
    }

    private void teamInviteRequest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to invite.");
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " is not online.");
            return;
        }
        if (teamManager.getTeam((Player) sender) == teamManager.getTeam(target)) {
            sender.sendMessage(ChatColor.RED + "Player is already in your team.");
            return;
        }
        if (!teamManager.teamInviteRequest((Player) sender, target)) {
            sender.sendMessage(ChatColor.RED + "Player already in another team.");
        }
    }

    private void leaveTeam(CommandSender sender) {
        Team team = teamManager.getTeam((Player) sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a team.");
            return;
        }
        teamManager.leaveTeam((Player) sender);
    }

    public void teamTeleportRequest(CommandSender sender, String[] args) {
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
            sender.sendMessage(ChatColor.RED + "You cannot teleport to that player.");
        }
    }

    public void teamTeleportAccept(CommandSender sender) {
        if (!teamManager.teamTeleportAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending teleport requests.");
        }
    }

    @Override
    public List<String> parseTabCompletions(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
