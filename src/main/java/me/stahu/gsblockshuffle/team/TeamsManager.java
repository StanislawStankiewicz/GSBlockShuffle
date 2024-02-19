package me.stahu.gsblockshuffle.team;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TeamsManager {
    public final HashSet<Team> teams= new HashSet<>();
    public final HashMap<Player, Team> teamCaptains = new HashMap<>();
    /**
     * A HashMap that stores team join requests in the game.
     * The key is the target of the request, and value is the sender.
     */
    public final HashMap<Player, Player> teamRequests = new HashMap<>();
    /**
     * A HashMap that stores teleport requests between players in the game.
     * The key is the player (Player) who sent the request, and the value is the player who received the request.
     */
    public final HashMap<Player, Player> teleportRequests = new HashMap<>();
    private ScoreboardManager scoreboardManager;
    private final Scoreboard scoreboard;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;


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

    public void joinTeamRequest(Player player, Team team) {
        plugin.sendMessage(player, "You have requested to join team " + team.getDisplayName());
        plugin.sendMessage(getTeamCaptain(team), player.getName() + " has requested to join your team. To accept type: /gsblockshuffle team accept");
        teamRequests.put(getTeamCaptain(team), player);
    }

    // TODO rename senders and targets
    public boolean joinTeamRequestAccept(Player captain){
        Player player = teamRequests.get(captain);
        if(player == null || teamCaptains.get(captain) == null){
            return false;
        }
        Team team = teamCaptains.get(captain);
        addPlayerToTeam(player, team, true);
        teamRequests.remove(captain);

        plugin.sendMessage(player, "You have been added to team " + team.getDisplayName());
        plugin.sendMessage(captain, player.getName() + " has been added to your team.");

        return true;
    }
    /**
     * Sends a team invite from the sender (captain) to the target player.
     * If the target player is already in a team or the sender is not a team captain, the method returns false.
     * If the invite is successfully sent, the method returns true.
     *
     * @param sender The player who is sending the invite.
     * @param target The player who is receiving the invite.
     * @return boolean Returns true if the invite is successfully sent, false otherwise.
     */
    public boolean teamInviteRequest(Player sender, Player target) {
        if (getTeam(target) != null){
            return false;
        }
        if(!teamCaptains.containsKey(sender)){
            return false;
        }
        plugin.sendMessage(sender, "You have invited " + target.getName() + " to your team.");
        plugin.sendMessage(target, "You have received an invite from " + sender.getName() + " to join their team. To accept type: /gsblockshuffle team accept");
        teamRequests.put(target, sender);
        return true;
    }

    /**
     * Accepts a pending team invite for the sender (player).
     * If the sender does not have any pending team invites, the method returns false.
     * If the invite is successfully accepted, the sender is added to the team and the invite is removed from the pending invites.
     *
     * @param sender The player who is accepting the invite.
     * @return boolean Returns true if the invite is successfully accepted, false otherwise.
     */
    public boolean teamInviteRequestAccept(Player sender) {
        if (teamRequests.containsKey(sender)) {
            Player captain = teamRequests.get(sender);
            Team team = getTeam(captain);
            addPlayerToTeam(sender, team, true);
            teamRequests.remove(sender);
            plugin.sendMessage(sender, "You have been added to team " + team.getDisplayName());
            return true;
        }
        return false;
    }

    // TODO rename sender and target as they mean different things in different methods
    public boolean teamTeleportRequest(Player sender, Player target){
        Team senderTeam = getTeam(sender);
        Team targetTeam = getTeam(target);

        if(senderTeam == null || targetTeam == null){
            return false;
        }

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

    /**
     * Handles the complete removal of a team from the game.
     *
     * @param team The team to be removed.
     */
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

    private void setTeamScore(Team team, int score) {
        scoreboard.getObjective("Score").getScore(team.getName()).setScore(score);
    }

    public void incrementTeamScore(Team team) {
        setTeamScore(team, getTeamScore(team) + 1);
    }

    public Team getTeam(String teamName) {
        return scoreboard.getTeam(teamName);
    }

    public Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
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

    public void leaveTeam(Player sender) {
        Team team = getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a team.");
            return;
        }
        removePlayerFromTeam(sender, team);
        if(team.getEntries().isEmpty()){
            removeTeam(team);
        }
        sender.sendMessage(ChatColor.GREEN + "You have left your team.");
    }
}