package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.CommandBase;
import me.stahu.gsblockshuffle.team.BSTeam;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamSubcommand extends CommandBase implements Subcommand {
    private final GSBlockShuffle plugin;
    private final TeamsManager teamManager;
    private final Map<String, ChatColor> stringToColorMap = new HashMap<>();

    public TeamSubcommand(GSBlockShuffle plugin, TeamsManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;

        stringToColorMap.put("dark_red", ChatColor.DARK_RED);
        stringToColorMap.put("red", ChatColor.RED);
        stringToColorMap.put("gold", ChatColor.GOLD);
        stringToColorMap.put("yellow", ChatColor.YELLOW);
        stringToColorMap.put("dark_green", ChatColor.DARK_GREEN);
        stringToColorMap.put("green", ChatColor.GREEN);
        stringToColorMap.put("aqua", ChatColor.AQUA);
        stringToColorMap.put("dark_aqua", ChatColor.DARK_AQUA);
        stringToColorMap.put("dark_blue", ChatColor.DARK_BLUE);
        stringToColorMap.put("blue", ChatColor.BLUE);
        stringToColorMap.put("light_purple", ChatColor.LIGHT_PURPLE);
        stringToColorMap.put("dark_purple", ChatColor.DARK_PURPLE);
        stringToColorMap.put("white", ChatColor.WHITE);
        stringToColorMap.put("gray", ChatColor.GRAY);
        stringToColorMap.put("dark_gray", ChatColor.DARK_GRAY);
        stringToColorMap.put("black", ChatColor.BLACK);
    }

    public void parseSubcommand(CommandSender sender, Command command, String label, String[] args) {
        // tell player his team and team members
        if (args.length == 1) {
            BSTeam team = teamManager.getTeam((Player) sender);
            if (team == null) {
                sender.sendMessage(ChatColor.RED + "You are not on a team.");
                return;
            }
            sender.sendMessage(ChatColor.GRAY + "Team " + team.getDisplayName() + ChatColor.GRAY + " members:");
            for (Player player : team.getPlayers()) {
                String message = " ● ";
                if (team.captain.equals(player)) {
                    message += ChatColor.AQUA + player.getName();
                } else {
                    message += player.getName();
                }
                sender.sendMessage(message);
            }
            return;
        }

        //check if player has permission to execute the subcommand
        if (!sender.hasPermission("BlockShuffle.command.team." + args[0])) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
        }

        switch (args[1].toLowerCase()) {
            // keep alphabetical order
            case "accept" -> teamAccept(sender);
            case "add" -> teamAdd(sender, args);
            case "color" -> changeTeamColor(sender, args);
            case "create" -> createTeam(sender, args);
            case "invite" -> teamInviteRequest(sender, args);
            case "join" -> teamJoinRequest(sender, args);
            case "leave" -> leaveTeam(sender);
            case "remove" -> teamRemove(sender, args);
            case "tp" -> teamTeleportRequest(sender, args);
            case "tpaccept" -> teamTeleportAccept(sender);
        }
    }

    private void createTeam(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team name.");
            return;
        }
        // check if team already exists
        BSTeam team = teamManager.getTeam(args[2]);

        if (team != null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.RED + " already exists.");
            return;
        }
        team = teamManager.createTeam(args[2], ChatColor.WHITE);
        team.addPlayer((Player) sender);
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.RESET + team.getDisplayName() + ChatColor.GREEN + " has been created.");
    }

    private void changeTeamColor(CommandSender sender, String[] args) {
        BSTeam team = teamManager.getTeam((Player) sender);
        //check if sender is the captain of his team "You must be a captain of a team to change it's color"
        if (team == null || team.captain.equals(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be a captain of a team to change it's color.");
            return;
        }
        // check if args[2] is a valid color like ChatColor.RED
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a color.");
            return;
        }
        if (!stringToColorMap.containsKey(args[2])) {
            sender.sendMessage(ChatColor.RED + "Invalid color.");
            return;
        }

        ChatColor color = stringToColorMap.get(args[2]);
        team.setColor(color);

        sender.sendMessage(ChatColor.GREEN + "Team color has been changed to " + color + args[2] + ChatColor.GREEN + ".");
    }

    // TODO refactor getTeam calls
    private void teamAdd(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to add to your team.");
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
        if (teamManager.getTeam(target) != null) {
            sender.sendMessage(ChatColor.RED + "Player is already in another team.");
            return;
        }
        teamManager.getTeam((Player) sender).addPlayer(target);
        sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.DARK_AQUA + target.getName() + ChatColor.GREEN + " has been added to your team.");
        target.sendMessage(ChatColor.GREEN + "You have been added to team " + ChatColor.DARK_AQUA + teamManager.getTeam((Player) sender).getDisplayName());
    }

    private void teamRemove(CommandSender sender, String[] args) {
        BSTeam senderTeam = teamManager.getTeam((Player) sender);
        if (senderTeam.captain.equals(sender) && args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You are not the captain of your team.");
            return;
        } else if (args.length == 2 && !sender.hasPermission("BlockShuffle.command.team.remove")) {
            sendTeamRemoveMessage(teamManager.getTeam((Player) sender));
            teamManager.removeTeam(teamManager.getTeam((Player) sender));
            return;
        }
        if(!sender.hasPermission("BlockShuffle.command.team.remove")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to remove other teams.");
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team to remove.");
            return;
        }
        BSTeam team = teamManager.getTeam(args[2]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " does not exist.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.GREEN + " has been successfully removed.");
        sendTeamRemoveMessage(team);
        teamManager.removeTeam(team);
    }

    private void sendTeamRemoveMessage(BSTeam team) {
        for (Player player : team.getPlayers()) {
            plugin.sendMessage(player, "Your team has been removed.");
        }
    }

    private void teamJoinRequest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team to join.");
            return;
        }
        BSTeam team = teamManager.getTeam(args[2]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " does not exist.");
            return;
        }
        teamManager.teamJoinRequest((Player) sender, team);
    }

    private void teamInviteRequest(CommandSender sender, String[] args) {
        if (teamManager.getTeam((Player) sender) == null) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to invite players.");
            return;
        }
        if(teamManager.getTeam((Player) sender).captain.equals(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be a captain of a team to invite players.");
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a player to invite.");
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " is not online.");
            return;
        }
        if (teamManager.getTeam(target) != null) {
            sender.sendMessage(ChatColor.RED + "Player is already in a team.");
            return;
        }
        teamManager.teamInvite((Player) sender, target);
    }

    /**
     * This method is used to handle the acceptance of team invitations or join requests.
     *
     * @param sender The sender of the command, usually a player.
     */
    private void teamAccept(CommandSender sender) {
        BSTeam senderTeam = teamManager.getTeam((Player) sender);
        if (senderTeam.captain.equals(sender)) {
            if (!teamManager.teamJoinAccept((Player) sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have any pending team requests.");
                return;
            }
            return;
        }
        // accept invite
        // sender should have no team if this method was called
        if (!teamManager.teamInviteAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending team invites.");
        }
    }

    private void leaveTeam(CommandSender sender) {
        BSTeam team = teamManager.getTeam((Player) sender);
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
        teamManager.teamTeleportRequest((Player) sender, target);
    }

    public void teamTeleportAccept(CommandSender sender) {
        if (!teamManager.teamTeleportAccept((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have any pending teleport requests.");
        }
    }

    @Override
    public List<String> parseTabCompletions(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            final List<String> suggestions = getAllowedCommands(sender);

            return filterCompletions(suggestions, args[1]);
        }
        if (args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "add", "invite", "tp" -> {
                    return filterCompletions(playerList(), args[2]);
                }
                case "join", "remove" -> {
                    return filterCompletions(teamManager.getTeams().stream().map(BSTeam::getName).toList(), args[2]);
                }
                case "color" -> {           // convert set to list
                    return filterCompletions(new ArrayList<>(stringToColorMap.keySet()), args[2]);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getAllowedCommands(CommandSender sender) {
        List<String> completions = List.of(
                // keep alphabetical order
                "accept",
                "add",
                "color",
                "create",
                "invite",
                "join",
                "leave",
                "remove",
                "tp",
                "tpaccept");

        final List<String> suggestions = new LinkedList<>();

        completions.forEach(cmd -> {
            if (sender.hasPermission("BlockShuffle.command.team." + cmd)) {
                suggestions.add(cmd);
            }
        });
        return suggestions;
    }
}
