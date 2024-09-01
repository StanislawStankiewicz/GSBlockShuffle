package me.stahu.gsblockshuffle.manager;

import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Optional;
import java.util.Set;

public interface PlayerManager {

    Set<Player> getPlayers();

    Set<Team> getTeams();

    void assignDefaultTeams();

    Optional<Player> getPlayer(PlayerAPI playerAPI);

    void resetBlocks();
}
