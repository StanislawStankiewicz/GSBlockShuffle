package me.stahu.gsblockshuffle.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Player;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class PlayersManager {

    @Getter
    final Set<Player> players;

    public Optional<Player> getPlayer(org.bukkit.entity.Player bukkitPlayer) {
        return players.stream()
                .filter(player -> player.getServerPlayer().equals(bukkitPlayer))
                .findFirst();
    }

    public Player addPlayer(org.bukkit.entity.Player bukkitPlayer) {
        Player player = new Player(bukkitPlayer);
        players.add(player);
        return player;
    }

    public void logoutPlayer(org.bukkit.entity.Player bukkitPlayer) {
        players.stream()
                .filter(player -> player.getServerPlayer().equals(bukkitPlayer))
                .findFirst()
                .ifPresent(player -> player.setOnline(false));
    }
}
