package me.stahu.gsblockshuffle.controller;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.team.*;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class TeamsController {

    final BlockShuffleEventDispatcher dispatcher;

    final Set<Team> teams;
    final Map<Player, Team> invites;
    final Map<Team, Player> requests;

    public void createTeam(Player player, String name) {
        if (isPlayerInTeam(player)) {
            return;
        }
        Team team = new Team(player, name);
        teams.add(team);
        player.setTeam(team);
        dispatcher.dispatch(new CreateTeamEvent(player, team));
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

    public void changeColor(Team team) {
        // to be implemented
    }

    public void addPlayerToTeam(Player player, Team team) {
        if (isPlayerInTeam(player)) {
            return;
        }
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
        if (isPlayerInTeam(player)) {
            return;
        }
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

    private boolean isPlayerInNoTeam(Player player) {
        if (player.getTeam() == null) {
            dispatcher.dispatch(new TeamFailEvent(player, TeamFailReason.NO_TEAM));
            return true;
        }
        return false;
    }

    private boolean isPlayerInTeam(Player player) {
        if (player.getTeam() != null) {
            dispatcher.dispatch(new TeamFailEvent(player, TeamFailReason.ALREADY_IN_TEAM));
            return true;
        }
        return false;
    }

    private boolean isNotLeader(Player player) {
        if (isPlayerInNoTeam(player)) {
            return true;
        }
        if (!player.getTeam().getLeader().equals(player)) {
            dispatcher.dispatch(new TeamFailEvent(player, TeamFailReason.NOT_A_LEADER));
            return true;
        }
        return false;
    }
}
