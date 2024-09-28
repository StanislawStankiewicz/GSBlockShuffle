package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Optional;
import java.util.Set;

public class PlayerManager {

    final Set<Team> teams;
    final Set<Player> players;

    public PlayerManager(Set<Team> teams, Set<Player> players) {
        this.teams = teams;
        this.players = players;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void assignDefaultTeams() {
        Team team;
        for (Player player : players) {
            if (player.getTeam() == null) {
                team = new Team(player);
                teams.add(team);
                player.setTeam(team);
            }
        }
    }

    public Optional<Player> getPlayer(PlayerAPI playerAPI) {
        return players.stream()
                .filter(player -> player.getPlayerAPI().equals(playerAPI))
                .findFirst();
    }

    public void resetBlocks() {
        for (Player player : players) {
            player.setFoundBlock(false);
        }
    }
}
