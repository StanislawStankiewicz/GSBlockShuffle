package me.stahu.gsblockshuffle.controller;

import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.type.team.KickFromTeamEvent;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamsControllerTests {

    @Mock
    private BlockShuffleEventDispatcher dispatcher;

    @Mock
    private Player player;

    @Mock
    private Player leader;

    @Mock
    private Team team;

    @InjectMocks
    private TeamsController teamsController;

    private Set<Team> teams;

    @BeforeEach
    public void setUp() {
        teams = new HashSet<>();
        MockitoAnnotations.openMocks(this);
        teamsController = new TeamsController(dispatcher, teams);
    }

    @Test
    void testCreateTeam() {
        String teamName = "TeamA";
        when(player.getTeam()).thenReturn(null);

        teamsController.createTeam(player, teamName);

        assertEquals(1, teams.size());
        Team createdTeam = teams.iterator().next();
        assertEquals(teamName, createdTeam.getName());
        assertTrue(createdTeam.getPlayers().contains(player));
        verify(player).setTeam(createdTeam);
    }

    @Test
    void testRemoveTeam() {
        teams.add(team);
        when(team.getPlayers()).thenReturn(Set.of(player));

        teamsController.removeTeam(team);

        assertFalse(teams.contains(team));
        verify(player).setTeam(null);
    }

    @Test
    void testLeaveTeam() {
        when(player.getTeam()).thenReturn(team);

        teamsController.leaveTeam(player);

        verify(team).removePlayer(player);
        verify(player).setTeam(null);
        assertFalse(teams.contains(team));
    }

    @Test
    void testAddPlayerToTeam() {
        when(team.getPlayers()).thenReturn(new HashSet<>());
        when(player.getTeam()).thenReturn(null);

        teamsController.addPlayerToTeam(player, team);

        verify(team).addPlayer(player);
        verify(player).setTeam(team);
    }

    @Test
    void testInvitePlayerToTeam() {
        when(leader.getTeam()).thenReturn(team);

        teamsController.invitePlayerToTeam(leader, player);

        assertEquals(team, teamsController.invites.get(player));
    }

    @Test
    void testAcceptInvite() {
        teamsController.invites.put(player, team);

        boolean result = teamsController.acceptInvite(player);

        assertTrue(result);
        verify(team).addPlayer(player);
        assertFalse(teamsController.invites.containsKey(player));
    }

    @Test
    void testRequestToJoinTeam() {
        teamsController.requestToJoinTeam(player, team);

        assertEquals(player, teamsController.requests.get(team));
    }

    @Test
    void testAcceptRequest() {
        when(leader.getTeam()).thenReturn(team);
        teamsController.requests.put(team, player);

        boolean result = teamsController.acceptRequest(leader);

        assertTrue(result);
        verify(team).addPlayer(player);
        assertFalse(teamsController.requests.containsKey(team));
    }

    @Test
    void testKickFromTeam() {
        when(leader.getTeam()).thenReturn(team);
        when(team.getPlayers()).thenReturn(new HashSet<>(Set.of(player)));

        teamsController.kickFromTeam(leader, player);

        verify(team).removePlayer(player);
        verify(player).setTeam(null);

        ArgumentCaptor<KickFromTeamEvent> eventCaptor = ArgumentCaptor.forClass(KickFromTeamEvent.class);
        verify(dispatcher).dispatch(eventCaptor.capture());

        KickFromTeamEvent capturedEvent = eventCaptor.getValue();
        assertEquals(leader, capturedEvent.leader());
        assertEquals(player, capturedEvent.player());
    }
}
