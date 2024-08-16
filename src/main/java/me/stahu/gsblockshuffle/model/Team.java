package me.stahu.gsblockshuffle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class Team {
    final String name;
    final Set<Player> players = new HashSet<>();
    final Player leader;
    boolean isEliminated;
    int score;

    public Team(Player leader) {
        this.name = leader.getDisplayName();
        this.leader = leader;
        this.players.add(leader);
        this.isEliminated = false;
        this.score = 0;
    }
}
