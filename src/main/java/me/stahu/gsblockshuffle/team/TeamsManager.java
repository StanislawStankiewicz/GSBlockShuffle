package me.stahu.gsblockshuffle.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class TeamsManager {
    public HashSet<Team> teams;
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;

    public TeamsManager() {
        this.teams = new HashSet<>();
        this.scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        this.scoreboard = scoreboardManager.getNewScoreboard();
    }

    public TeamsManager(HashSet<Team> teams) {
        this.teams = teams;
    }

    public void setUpScoreboard() {
        Objective objective = scoreboard.registerNewObjective("Score", "dummy", "Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Team team : teams) {
            Score score = objective.getScore(team.getName());
            score.setScore(0);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public void handleRemainingPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            System.out.println("Now handling: " + player.getName());
            if (!isPlayerInAnyTeam(player)) {
                System.out.println(isPlayerInAnyTeam(player));
                addTeam(player.getName(), ChatColor.WHITE);
                addPlayerToTeam(player, player.getName());
                System.out.println("is this player in any team now?: " + isPlayerInAnyTeam(player));
            }
        }
    }

    public HashSet<Player> getPlayersWithATeam() {
        HashSet<Player> playersWithATeam = new HashSet<>();
        for (Team team : teams) {
            HashSet<Player> players = new HashSet<>();
            for (String entry : team.getEntries()) {
                players.add(Bukkit.getPlayer(entry));
            }
            playersWithATeam.addAll(players);
        }
        return playersWithATeam;
    }

    public void addTeam(String teamName, ChatColor color) {
        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(color);
        teams.add(team);
    }

    public void addPlayerToTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        assert team != null;
        team.addEntry(player.getName());
    }

    public void removePlayerFromTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        assert team != null;
        team.removeEntry(player.getName());
    }

    public void removeTeam(String teamName) {
        Team team = scoreboard.getTeam(teamName);
        assert team != null;
        team.unregister();
    }

    public boolean isPlayerInTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        assert team != null;
        return team.hasEntry(player.getName());
    }

    public boolean isPlayerInAnyTeam(Player player) {
        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public Team getPlayerTeam(Player player) {
        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }

    public int getTeamScore(Team team) {
        return scoreboard.getObjective("Score").getScore(team.getName()).getScore();
    }

    private void setTeamScore(Team team, int score) {
        scoreboard.getObjective("Score").getScore(team.getName()).setScore(score);
    }

    public void incrementTeamScore(Team team) {
        setTeamScore(team, getTeamScore(team) + 1);
    }

    public Team getTeam(String teamName) {
        return scoreboard.getTeam(teamName);
    }

    public void clearScoreboards() {
        scoreboard = scoreboardManager.getNewScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public ArrayList<Team> getSortedTeams() {
        ArrayList<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort(Comparator.comparingInt(this::getTeamScore));
        return sortedTeams;
    }
}