package me.stahu.gsblockshuffle.controller;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.team.*;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class TeamsController {

    final BlockShuffleEventDispatcher dispatcher;

    final Set<Team> teams;
    final Map<Player, Team> invites = new HashMap<>();
    final Map<Team, Player> requests = new HashMap<>();

    public Optional<Team> findTeamByName(String name) {
        return teams.stream()
                .filter(team -> team.getName().equals(name))
                .findFirst();
    }

    public void createTeam(Player player, String name) {
        Team team = new Team(player, name);
        teams.add(team);
        player.setTeam(Optional.of(team));
        dispatcher.dispatch(new CreateTeamEvent(team, player));
    }

    public void removeTeam(Team team) {
        team.getPlayers().forEach(player -> player.setTeam(Optional.empty()));
        teams.remove(team);
        dispatcher.dispatch(new RemoveTeamEvent(team));
    }

    public void leaveTeam(Player player) {
        Team team = player.getTeam()
                .orElseThrow();
        team.removePlayer(player);
        player.setTeam(Optional.empty());
        dispatcher.dispatch(new LeaveTeamEvent(team, player));
    }

    public void changeColor(Team team, ChatColor color) {
        // to be implemented
    }

    public void addPlayerToTeam(Player player, Team team) {
        team.addPlayer(player);
        player.setTeam(Optional.of(team));
        dispatcher.dispatch(new AddPlayerToTeamEvent(team, player));
    }

    public void invitePlayerToTeam(Player inviter, Player player) {
        invites.put(player, inviter.getTeam()
                .orElseThrow());
        dispatcher.dispatch(new InvitePlayerToTeamEvent(inviter, player));
    }

    public boolean acceptInvite(Player player) {
        Team team = invites.get(player);
        if (team == null) {
            return false;
        }
        addPlayerToTeam(player, team);
        invites.remove(player);
        dispatcher.dispatch(new AcceptInviteEvent(team, player));
        return true;
    }

    public void requestToJoinTeam(Player player, Team team) {
        requests.put(team, player);
        dispatcher.dispatch(new RequestToJoinTeamEvent(team, player));
    }

    public boolean acceptRequest(Player leader) {
        Team team = leader.getTeam()
                .orElseThrow();
        Player player = requests.get(team);
        if (player == null) {
            return false;
        }
        requests.remove(team);
        addPlayerToTeam(player, team);
        dispatcher.dispatch(new AcceptRequestEvent(leader, player));
        return true;
    }

    public void kickFromTeam(Player leader, Player player) {
        Team team = leader.getTeam()
                .orElseThrow();
        team.removePlayer(player);
        player.setTeam(Optional.empty());
        dispatcher.dispatch(new KickFromTeamEvent(leader, player));
    }
}
