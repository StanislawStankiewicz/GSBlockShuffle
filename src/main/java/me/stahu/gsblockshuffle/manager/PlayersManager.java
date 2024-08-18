package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.model.Player;

import java.util.Optional;

public interface PlayersManager {
    void assignTeams();

    Optional<Player> getPlayer(org.bukkit.entity.Player bukkitPlayer);

    void resetBlocks();
}
