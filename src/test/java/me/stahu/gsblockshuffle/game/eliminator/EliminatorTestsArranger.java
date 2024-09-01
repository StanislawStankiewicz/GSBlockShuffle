package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

public class EliminatorTestsArranger {

    public static Set<Team> arrangeTeams(int teamCount, int playerCount) {
        Set<Team> teams = new HashSet<>();
        for (int i = 0; i < teamCount; i++) {
            Set<Player> players = new HashSet<>();
            for (int j = 0; j < playerCount; j++) {
                PlayerAPI playerApi = Mockito.mock(PlayerAPI.class);
                Player player = new Player(playerApi);
                player.setFoundBlock(false);
                players.add(player);
            }
            Team team = new Team(players.iterator().next());
            team.getPlayers().addAll(players);
            teams.add(team);
        }
        return teams;
    }
}