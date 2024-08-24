package me.stahu.gsblockshuffle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class Team {
    final String name;
    final Set<Player> players = new HashSet<>();
    Player leader;
    boolean isEliminated;
    int score;

    public Team(Player leader) {
        this(leader, leader.getName());
    }

    public Team(Player player, String name) {
        this.name = name;
        this.leader = player;
        this.players.add(player);
        this.isEliminated = false;
        this.score = 0;
    }

    public String getDisplayName() {
        return name;
    }

    public void addPlayer(Player player) {
        if (players.isEmpty()) {
            leader = player;
        }
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (player.equals(leader)) {
            leader = players.stream().findFirst().orElse(null);
        }
    }
}
