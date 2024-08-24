package me.stahu.gsblockshuffle.api;

import me.stahu.gsblockshuffle.model.Player;
import org.bukkit.Bukkit;

import java.util.Set;

public class BukkitAPI implements ServerAPI {

    @Override
    public Set<Player> getPlayers() {
        return Set.of(
                Bukkit.getOnlinePlayers().stream()
                        .map(player -> new Player(new BukkitPlayerAPI(player)))
                        .toArray(Player[]::new));
    }

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }
}
