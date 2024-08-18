package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Optional;
import java.util.Set;

public class PlayersManagerImpl implements PlayersManager {

    final Set<Team> teams;
    Set<Player> players;

    public PlayersManagerImpl(Set<Team> teams, Set<Player> players) {
        this.teams = teams;
        this.players = players;
    }

    @Override
    public void assignTeams() {
        Team team;
        for (Player player : players) {
            if (player.getTeam() == null) {
                team = new Team(player);
                teams.add(team);
                player.setTeam(team);
            }
        }
    }

    @Override
    public Optional<Player> getPlayer(org.bukkit.entity.Player bukkitPlayer) {
        return players.stream()
                .filter(player -> player.getPlayer().equals(bukkitPlayer))
                .findFirst();
    }

    @Override
    public void resetBlocks() {
        for (Player player : players) {
            player.setFoundBlock(false);
        }
    }
}
