package me.stahu.gsblockshuffle.model;

import lombok.Getter;

import java.util.Set;

@Getter
public class Team {
    final String name;
    final Set<Player> players;
    final Player leader;
    final org.bukkit.scoreboard.Team scoreboardTeam;
    boolean isEliminated;
    int score;

    public Team(Player leader) {
        this.name = leader.getDisplayName();
        this.leader = leader;
        this.scoreboardTeam = leader.getScoreboard().registerNewTeam(name);
        this.players = Set.of(leader);
        this.isEliminated = false;
        this.score = 0;
    }
}
