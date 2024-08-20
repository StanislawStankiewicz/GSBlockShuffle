package me.stahu.gsblockshuffle.controller;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.team.*;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Map;
import java.util.Set;

// TODO refactor:
//  - create methods for getting a team that will fail if no team exists for use in commands
//  - perform validation in separate methods
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
        team.getPlayers().forEach(player -> player.setTeam(null));
        teams.remove(team);
        dispatcher.dispatch(new RemoveTeamEvent(team));
    }

    public void removeOwnTeam(Player player) {
        if (isPlayerInNoTeam(player)) {
            return;
        }
        removeTeam(player.getTeam());
    }

    public void leaveTeam(Player player) {
        if (isPlayerInNoTeam(player)) {
            return;
        }
        Team team = player.getTeam();
        player.getTeam().removePlayer(player);
        player.setTeam(null);
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
        player.setTeam(team);
        dispatcher.dispatch(new AddPlayerToTeamEvent(team, player));
    }

    public void invitePlayerToTeam(Player inviter, Player player, Team team) {
        if (isNotLeader(inviter) || isPlayerInTeam(player)) {
            return;
        }
        invites.put(player, team);
        dispatcher.dispatch(new InvitePlayerToTeamEvent(inviter, player));
    }

    public void acceptInvite(Player player) {
        Team team = invites.get(player);
        if (team == null) {
            dispatcher.dispatch(new TeamFailEvent(player, TeamFailReason.NO_TEAM));
            return;
        }
        addPlayerToTeam(player, team);
        invites.remove(player);
        dispatcher.dispatch(new AcceptInviteEvent(team, player));
    }

    public void requestToJoinTeam(Player player, Team team) {
        if (isPlayerInTeam(player)) {
            return;
        }
        requests.put(team, player);
        dispatcher.dispatch(new RequestToJoinTeamEvent(team, player));
    }

    public void acceptRequest(Player leader) {
        if (isNotLeader(leader)) {
            return;
        }
        Player player = requests.get(leader.getTeam());
        if (player == null) {
            dispatcher.dispatch(new TeamFailEvent(leader, TeamFailReason.NO_SUCH_REQUEST));
            return;
        }
        requests.remove(leader.getTeam());
        addPlayerToTeam(player, leader.getTeam());
        dispatcher.dispatch(new AcceptRequestEvent(leader, player));
    }

    public void kickFromTeam(Player leader, Player player) {
        if (isNotLeader(leader)) {
            return;
        }
        if (!leader.getTeam().getPlayers().contains(player)) {
            dispatcher.dispatch(new TeamFailEvent(leader, TeamFailReason.NO_SUCH_PLAYER));
            return;
        }
        leader.getTeam().removePlayer(player);
        player.setTeam(null);
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
