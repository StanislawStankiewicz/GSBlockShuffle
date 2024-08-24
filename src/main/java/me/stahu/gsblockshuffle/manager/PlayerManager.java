package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Player;

import java.util.Optional;

public interface PlayerManager {
    void assignDefaultTeams();

    Optional<Player> getPlayer(PlayerAPI playerAPI);

    void resetBlocks();
}
