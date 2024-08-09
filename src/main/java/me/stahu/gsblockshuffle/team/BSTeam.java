package me.stahu.gsblockshuffle.team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class BSTeam {
    public static BSTeamsManager teamsManager;
    public static Scoreboard scoreboard;

    private final Team scoreboardTeam;
    private final Set<Player> players;
    private int score;
    public Player captain;
    public boolean isEliminated;

    public Set<Player> getPlayers() {
        return players;
    }

    public int getScore() {
        return score;
    }

    public void incScore() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }

    public BSTeam(String teamName) {
        this.scoreboardTeam = scoreboard.registerNewTeam(teamName);
        this.players = new HashSet<>();
        this.isEliminated = false;
        this.score = 0;
    }

    public void addPlayer(Player player) {
        scoreboardTeam.addEntry(player.getName());
        players.add(player);
    }

    public void removePlayer(Player player) {
        scoreboardTeam.removeEntry(player.getName());
        players.remove(player);
    }

    public void setColor(ChatColor color) {
        scoreboardTeam.setColor(color);
        scoreboardTeam.setColor(color);
        scoreboardTeam.setPrefix(color.toString());
        updateColor();
    }

    private void updateColor() {
        this.setDisplayName(this.getColor() + this.getName());
        for (Player player : this.getPlayers()) {
            if (player != null) {
                player.setDisplayName(this.getColor() + player.getName() + ChatColor.RESET);
                player.setScoreboard(scoreboard);
            }
        }
        teamsManager.setScoreboard();
    }

    public String getName() {
        return scoreboardTeam.getName();
    }

    public String getDisplayName() {
        return scoreboardTeam.getDisplayName();
    }

    public void setDisplayName(String displayName) {
        scoreboardTeam.setDisplayName(displayName);
    }

    public ChatColor getColor() {
        return scoreboardTeam.getColor();
    }

    // Custom comparator based on score
    public static class ScoreComparator implements Comparator<BSTeam> {
        @Override
        public int compare(BSTeam team1, BSTeam team2) {
            return Integer.compare(team1.getScore(), team2.getScore());
        }
    }
}
