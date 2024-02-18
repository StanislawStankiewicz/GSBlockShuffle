package me.stahu.gsblockshuffle.team;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TeamsManager {
    public final HashSet<Team> teams= new HashSet<>();;
    public final HashMap<Player, Team> teamCaptains = new HashMap<>();
    public final HashMap<Player, Player> teamRequests = new HashMap<>();
    public final HashMap<Player, Player> teleportRequests = new HashMap<>();
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private YamlConfiguration settings;
    private GSBlockShuffle plugin;


    public TeamsManager(YamlConfiguration settings, GSBlockShuffle plugin) {
        this.scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.settings = settings;
        this.plugin = plugin;
    }

    public void handleRemainingPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isPlayerInAnyTeam(player)) {
                Team team = addTeam(player.getName(), ChatColor.WHITE);
                addPlayerToTeam(player, team, false);
                teamCaptains.put(player, team);
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

    public Team addTeam(String teamName, ChatColor color) {
        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(color);
        teams.add(team);

        return team;
    }

    public void addPlayerToTeam(Player player, Team team, boolean messagePlayer) {
        if(team.getEntries().isEmpty()) {
            teamCaptains.put(player, team);
            if(messagePlayer){
                plugin.sendMessage(player, "You are the captain of team " + team.getDisplayName());
            }
        }

        team.addEntry(player.getName());
    }

    public void addPlayerToTeamRequest(Player player, Team team) {
        plugin.sendMessage(player, "You have requested to join team " + team.getDisplayName());
        plugin.sendMessage(getTeamCaptain(team), player.getName() + " has requested to join your team. Type /gsblockshuffle team accept to accept.");
        teamRequests.put(getTeamCaptain(team), player);
    }

    public boolean teamRequestAccept(Player captain){
        Player player = teamRequests.get(captain);
        if(player == null){
            return false;
        }
        Team team = teamCaptains.get(captain);
        addPlayerToTeam(player, team, true);
        teamRequests.remove(captain);

        plugin.sendMessage(player, "You have been added to team " + team.getDisplayName());
        plugin.sendMessage(captain, player.getName() + " has been added to your team.");

        return true;
    }

    public boolean teamTeleportRequest(Player sender, Player target){
        Team senderTeam = getPlayerTeam(sender);
        Team targetTeam = getPlayerTeam(target);

        if(senderTeam.equals(targetTeam)){
            sender.sendMessage(ChatColor.GREEN + "Team request sent to: " + target.getName());
            target.sendMessage( ChatColor.GREEN + "You have received a teleport request from: " + sender.getName() + ". Type /gsblockshuffle tpaccept to accept.");
            teleportRequests.put(sender, target);
            return true;
        }
        return false;
    }

    public boolean teamTeleportAccept(Player sender){
        if (teleportRequests.containsKey(sender)) {
            Player target = teleportRequests.get(sender);
            handleTeamTeleport(sender, target);
            teleportRequests.remove(sender);
            return true;
        }
        return false;
    }

    public void handleTeamTeleport(Player sender, Player target){
        sender.teleport(target);
    }

    public void removePlayerFromTeam(Player player, Team team) {
        team.removeEntry(player.getName());
    }

    public void removeTeam(Team team) {
        Player captain = getTeamCaptain(team);
        teamCaptains.remove(captain);
        teamRequests.remove(captain);

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

    private void setTeamScore(Team team, int score) {
        scoreboard.getObjective("Score").getScore(team.getName()).setScore(score);
    }

    public void incrementTeamScore(Team team) {
        setTeamScore(team, getTeamScore(team) + 1);
    }

    public Team getTeam(String teamName) {
        return scoreboard.getTeam(teamName);
    }

    public void setUpScoreboard() {
        boolean showTeamCoords = settings.getBoolean("showTeamCoords");

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

    public int getTeamScore(Team team) {
        return scoreboard.getObjective("Score").getScore(team.getName()).getScore();
    }

    public void clearScoreboards() {
        Objective objective = scoreboard.getObjective("Score");
        if(objective != null){
            objective.unregister();
        }
    }

    public ArrayList<Team> getSortedTeams() {
        ArrayList<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort(Comparator.comparingInt(this::getTeamScore));
        return sortedTeams;
    }

    public Player getTeamCaptain(Team teamToSearch){
        for(Player captain : teamCaptains.keySet()){
            if(teamCaptains.get(captain).equals(teamToSearch)){
                return captain;
            }
        }
        return null;
    }
}