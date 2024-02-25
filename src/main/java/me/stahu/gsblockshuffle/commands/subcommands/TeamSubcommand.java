package me.stahu.gsblockshuffle.commands.subcommands;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.commands.CommandBase;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamSubcommand extends CommandBase implements Subcommand {
    private final GSBlockShuffle plugin;
    private final GameStateManager gameStateManager;
    private final YamlConfiguration settings;
    private final TeamsManager teamManager;
    private final Map<String, ChatColor> stringToColorMap = new HashMap<>();

    public TeamSubcommand(GSBlockShuffle plugin, GameStateManager gameStateManager, YamlConfiguration settings, TeamsManager teamManager) {
        this.plugin = plugin;
        this.gameStateManager = gameStateManager;
        this.settings = settings;
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

        //check if player has permission to execute the subcommand
        if (!sender.hasPermission("BlockShuffle.command.team." + args[0].toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
        }

        switch (args[1].toLowerCase()) {
            case "create" -> createTeam(sender, args);
            case "color" -> changeTeamColor(sender, args);
            case "add" -> teamAdd(sender, args);
            case "remove" -> teamRemove(sender, args);
            case "join" -> joinTeamRequest(sender, args);
            case "invite" -> teamInviteRequest(sender, args);
            case "accept" -> teamAccept(sender, args);
            case "leave" -> leaveTeam(sender);
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
        Team team = teamManager.getTeam(args[2]);

        if (teamManager.teams.contains(team)) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.RED + " already exists.");
            return;
        }
        team = teamManager.addTeam(args[2], ChatColor.WHITE);
        teamManager.addPlayerToTeam((Player) sender, teamManager.getTeam(args[2]), false);
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.RESET + team.getDisplayName() + ChatColor.GREEN + " has been created.");
    }
    private void changeTeamColor(CommandSender sender, String[] args) {
        //check if sender is the captain of his team "You must be a captain of a team to change it's color"
        if (!teamManager.teamCaptains.containsKey((Player) sender)) {
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
        teamManager.setTeamColor((Player) sender, color);
        sender.sendMessage(ChatColor.GREEN + "Team color has been changed to " + color + args[2] + ChatColor.GREEN + ".");
    }
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
        if(teamManager.getTeam(target) != null) {
            sender.sendMessage(ChatColor.RED + "Player is already in another team.");
            return;
        }
        teamManager.addPlayerToTeam(target, teamManager.getTeam((Player) sender), true);
        sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.DARK_AQUA + target.getName() + ChatColor.GREEN + " has been added to your team.");
        target.sendMessage(ChatColor.GREEN + "You have been added to team " + ChatColor.DARK_AQUA + teamManager.getTeam((Player) sender).getDisplayName());
    }
    private void teamRemove(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a team to remove.");
            return;
        }
        Team team = teamManager.getTeam(args[2]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team " + ChatColor.DARK_AQUA + args[2] + ChatColor.RED + " does not exist.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Team " + ChatColor.DARK_AQUA + team.getDisplayName() + ChatColor.GREEN + " has been successfully removed.");
        for(String playerName : team.getEntries()){
            Player player = Bukkit.getPlayer(playerName);
            plugin.sendMessage(player, "Your team has been removed.");
        }
        teamManager.removeTeam(team);
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
    /**
     * This method is used to handle the acceptance of team invitations or join requests.
     *
     * @param sender The sender of the command, usually a player.
     * @param args The arguments provided with the command.
     */
    private void teamAccept(CommandSender sender, String[] args) {
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
        if (args.length == 2) {
            final List<String> suggestions = getAllowedCommands(sender);

            return filterCompletions(suggestions, args[1]);
        }
        System.out.println("args[1]: " + args[1]);
        System.out.println(filterCompletions(teamManager.teams.stream().map(Team::getName).toList(), args[2]));
        System.out.println(filterCompletions(playerList(), args[2]));
        if(args.length == 3){
            switch(args[1].toLowerCase()){
                case "add", "invite", "tp" -> {
                    return filterCompletions(playerList(), args[2].toLowerCase());
                }
                case "join", "remove" -> {
                    return filterCompletions(teamManager.teams.stream().map(Team::getName).toList(), args[2]);
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
