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
    final org.bukkit.scoreboard.Team scoreboardTeam;
    boolean isEliminated;
    int score;

    public Team(Player leader) {
        this.name = leader.getDisplayName();
        this.leader = leader;
        this.scoreboardTeam = leader.getScoreboard().registerNewTeam(name);
        this.players.add(leader);
        this.isEliminated = false;
        this.score = 0;
    }
}
