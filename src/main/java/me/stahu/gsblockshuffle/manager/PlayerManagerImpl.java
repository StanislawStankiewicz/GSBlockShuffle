package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Optional;
import java.util.Set;

public class PlayerManagerImpl implements PlayerManager {

    final Set<Team> teams;
    Set<Player> players;

    public PlayerManagerImpl(Set<Team> teams, Set<Player> players) {
        this.teams = teams;
        this.players = players;
    }

    @Override
    public void assignDefaultTeams() {
        Team team;
        for (Player player : players) {
            if (player.getTeam().isEmpty()) {
                team = new Team(player);
                teams.add(team);
                player.setTeam(Optional.of(team));
            }
        }
    }

    @Override
    public Optional<Player> getPlayer(PlayerAPI playerAPI) {
        return players.stream()
                .filter(player -> player.getApi().equals(playerAPI))
                .findFirst();
    }

    @Override
    public void resetBlocks() {
        for (Player player : players) {
            player.setFoundBlock(false);
        }
    }
}
