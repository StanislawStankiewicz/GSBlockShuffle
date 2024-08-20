package me.stahu.gsblockshuffle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter @Setter
@AllArgsConstructor
public class Team {
    final String name;
    final Set<Player> players = new HashSet<>();
    Player leader;
    boolean isEliminated;
    int score;

    public Team(Player leader) {
        this.name = leader.getDisplayName();
        this.leader = leader;
        this.players.add(leader);
        this.isEliminated = false;
        this.score = 0;
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
