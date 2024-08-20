package me.stahu.gsblockshuffle.controller;

import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TeamsControllerTests {

    private TeamsController teamsController;
    private Player player;
    private Player inviter;
    private Player leader;
    private Team team;

    @BeforeEach
    void setUp() {
        BlockShuffleEventDispatcher eventDispatcher = mock(BlockShuffleEventDispatcher.class);
        teamsController = new TeamsController(eventDispatcher, new HashSet<>(), new HashMap<>(), new HashMap<>());
        player = mock(Player.class);
        inviter = mock(Player.class);
        leader = mock(Player.class);
        team = mock(Team.class);
    }

    @Test
    void createTeam_createsNewTeam() {
        when(player.getTeam()).thenReturn(null);

        teamsController.createTeam(player, "TeamName");

        verify(player).setTeam(any(Team.class));
    }

    @Test
    void createTeam_doesNothingIfPlayerAlreadyInTeam() {
        when(player.getTeam()).thenReturn(team);

        teamsController.createTeam(player, "TeamName");

        verify(team, never()).addPlayer(any(Player.class));
    }

    @Test
    void removeTeam_removesTeamAndSetsPlayersTeamToNull() {
        when(team.getPlayers()).thenReturn(Set.of(player));

        teamsController.removeTeam(team);

        verify(player).setTeam(null);
        assertFalse(teamsController.teams.contains(team));
    }

    @Test
    void removeOwnTeam_removesTeamIfPlayerIsInTeam() {
        when(player.getTeam()).thenReturn(team);

        teamsController.removeOwnTeam(player);

        verify(team).getPlayers();
        assertThat(teamsController.teams).isEmpty();
    }

    @Test
    void leaveTeam_removesPlayerFromTeam() {
        when(player.getTeam()).thenReturn(team);

        teamsController.leaveTeam(player);

        verify(team).removePlayer(player);
        verify(player).setTeam(null);
    }

    @Test
    void addPlayerToTeam_addsPlayerToTeam() {
        when(player.getTeam()).thenReturn(null);

        teamsController.addPlayerToTeam(player, team);

        verify(team).addPlayer(player);
        verify(player).setTeam(team);
    }

    @Test
    void invitePlayerToTeam_addsInviteIfConditionsMet() {
        when(player.getTeam()).thenReturn(null);
        when(inviter.getTeam()).thenReturn(team);
        when(team.getLeader()).thenReturn(inviter);
        when(inviter.getTeam().getLeader()).thenReturn(inviter);

        teamsController.invitePlayerToTeam(inviter, player, team);

        assertThat(teamsController.invites).containsEntry(player, team);
    }

    @Test
    void acceptInvite_addsPlayerToTeamAndRemovesInvite() {
        teamsController.invites.put(player, team);

        teamsController.acceptInvite(player);

        verify(team).addPlayer(player);
        assertFalse(teamsController.invites.containsKey(player));
    }

    @Test
    void requestToJoinTeam_addsRequestIfConditionsMet() {
        when(player.getTeam()).thenReturn(null);

        teamsController.requestToJoinTeam(player, team);

        assertTrue(teamsController.requests.containsKey(team));
    }

    @Test
    void acceptRequest_addsPlayerToTeamAndRemovesRequest() {
        when(player.getTeam()).thenReturn(null);
        when(leader.getTeam()).thenReturn(team);
        when(team.getLeader()).thenReturn(leader);
        when(leader.getTeam().getLeader()).thenReturn(leader);
        teamsController.requests.put(team, player);

        teamsController.acceptRequest(leader);

        verify(team).addPlayer(player);
        assertThat(teamsController.requests).doesNotContainKey(team);
    }

    @Test
    void kickFromTeam_removesPlayerFromTeamIfLeader() {
        when(leader.getTeam()).thenReturn(team);
        when(team.getLeader()).thenReturn(leader);
        when(team.getPlayers()).thenReturn(Set.of(player));

        teamsController.kickFromTeam(leader, player);

        verify(team).removePlayer(player);
        verify(player).setTeam(null);
    }
}