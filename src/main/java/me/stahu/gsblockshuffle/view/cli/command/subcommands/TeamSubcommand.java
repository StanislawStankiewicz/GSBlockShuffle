package me.stahu.gsblockshuffle.view.cli.command.subcommands;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.TeamsController;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.command.*;
import me.stahu.gsblockshuffle.manager.PlayerManager;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.cli.command.Command;
import org.bukkit.ChatColor;

import java.util.*;

@RequiredArgsConstructor
public class TeamSubcommand extends Command implements Subcommand {
    private final BlockShuffleEventDispatcher eventDispatcher;
    private final TeamsController teamsController;
    private final PlayerManager playerManager;

    public void parseSubcommand(Player sender, String[] args) {
        // tell player his team and team members
        if (args.length == 1) {
            if (sender.getTeam().isEmpty()) {
                eventDispatcher.dispatch(new NoTeamEvent(sender));
                return;
            }
            Team team = sender.getTeam().get();
            // TODO: Handle sending team information to the player
            return;
        }

        // check if player has permission to execute the subcommand
        if (!sender.hasPermission("BlockShuffle.command.team." + args[0])) {
            eventDispatcher.dispatch(new NoPermissionEvent(sender));
            return;
        }

        switch (args[1].toLowerCase()) {
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

    private void createTeam(Player sender, String[] args) {
        if (sender.getTeam().isPresent()) {
            eventDispatcher.dispatch(new AlreadyInTeamEvent(sender));
            return;
        }
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyTeamNameEvent(sender));
            return;
        }
        teamsController.createTeam(sender, args[2]);
    }

    private void changeTeamColor(Player sender, String[] args) {
        if (sender.getTeam().isEmpty()) {
            eventDispatcher.dispatch(new NoTeamEvent(sender));
            return;
        }
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyColorEvent(sender));
            return;
        }
        ChatColor color;
        try {
            color = ChatColor.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            eventDispatcher.dispatch(new InvalidColorEvent(sender));
            return;
        }
        teamsController.changeColor(sender.getTeam().get(), color);
    }

    private void teamAdd(Player sender, String[] args) {
        System.out.println("Adding player to team");
        if (sender.getTeam().isEmpty() || !sender.getTeam().get().getLeader().equals(sender)) {
            eventDispatcher.dispatch(new NotLeaderEvent(sender));
            return;
        }
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyPlayerEvent(sender));
            return;
        }
        Optional<Player> targetOpt = getPlayer(args[2]);
        if (targetOpt.isEmpty()) {
            eventDispatcher.dispatch(new NoSuchPlayerEvent(sender, args[2]));
            return;
        }
        Player target = targetOpt.get();
        if (sender.getTeam().get().getPlayers().contains(target)) {
            eventDispatcher.dispatch(new AlreadyInTeamEvent(sender));
            return;
        }
        if (target.getTeam().isPresent()) {
            eventDispatcher.dispatch(new AlreadyInTeamEvent(target));
            return;
        }
        System.out.println("Adding player to team");
        teamsController.addPlayerToTeam(target, sender.getTeam().get());
    }

    private void teamRemove(Player sender, String[] args) {
        if (sender.getTeam().isEmpty() || !sender.getTeam().get().getLeader().equals(sender)) {
            eventDispatcher.dispatch(new NotLeaderEvent(sender));
            return;
        }
        if (!sender.hasPermission("BlockShuffle.command.team.remove")) {
            eventDispatcher.dispatch(new NoPermissionEvent(sender));
            return;
        }
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyTeamNameEvent(sender));
            return;
        }
        Optional<Team> team = teamsController.findTeamByName(args[2]);
        if (team.isEmpty()) {
            eventDispatcher.dispatch(new NoSuchTeamEvent(sender, args[2]));
            return;
        }
        teamsController.removeTeam(team.get());
    }

    private void teamJoinRequest(Player sender, String[] args) {
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyTeamNameEvent(sender));
            return;
        }
        Optional<Team> team = teamsController.findTeamByName(args[2]);
        if (team.isEmpty()) {
            eventDispatcher.dispatch(new NoSuchTeamEvent(sender, args[2]));
            return;
        }
        teamsController.requestToJoinTeam(sender, team.get());
    }

    private void teamInviteRequest(Player sender, String[] args) {
        if (!sender.isLeader()) {
            eventDispatcher.dispatch(new NotLeaderEvent(sender));
            return;
        }
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyPlayerEvent(sender));
            return;
        }
        Optional<Player> targetOpt = getPlayer(args[2]);
        if (targetOpt.isEmpty()) {
            eventDispatcher.dispatch(new NoSuchPlayerEvent(sender, args[2]));
            return;
        }
        if (targetOpt.get().getTeam().isPresent()) {
            eventDispatcher.dispatch(new AlreadyInTeamEvent(targetOpt.get()));
            return;
        }
        teamsController.invitePlayerToTeam(sender, targetOpt.get());
    }

    private void teamAccept(Player sender) {
        if (sender.isLeader()) {
            if (!teamsController.acceptRequest(sender)) {
                eventDispatcher.dispatch(new NoSuchRequestEvent(sender));
                return;
            }
            return;
        }
        if (!teamsController.acceptInvite(sender)) {
            eventDispatcher.dispatch(new NoSuchInviteEvent(sender));
        }
    }

    private void leaveTeam(Player sender) {
        if (sender.getTeam().isEmpty()) {
            eventDispatcher.dispatch(new NoTeamEvent(sender));
            return;
        }
        teamsController.leaveTeam(sender);
    }

    public void teamTeleportRequest(Player sender, String[] args) {
        if (args.length == 2) {
            eventDispatcher.dispatch(new SpecifyPlayerEvent(sender));
            return;
        }
        Optional<Player> target = getPlayer(args[2]);
        if (target.isEmpty()) {
            eventDispatcher.dispatch(new NoSuchPlayerEvent(sender, args[2]));
            return;
        }
        // Implement team teleport request logic
    }

    public void teamTeleportAccept(Player sender) {
        // Implement team teleport accept logic
    }

    private Optional<Player> getPlayer(String name) {
        return playerManager.getPlayers().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<String> parseTabCompletions(Player sender, String[] args) {
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
                    return filterCompletions(playerManager.getTeams().stream().map(Team::getName).toList(), args[2]);
                }
                case "color" -> {
                    return filterCompletions(Arrays.stream(ChatColor.values()).map(ChatColor::toString).toList(), args[2]);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getAllowedCommands(Player sender) {
        List<String> completions = List.of(
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
