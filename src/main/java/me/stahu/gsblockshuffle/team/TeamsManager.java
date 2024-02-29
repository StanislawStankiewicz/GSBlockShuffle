package me.stahu.gsblockshuffle.team;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TeamsManager {
    public final HashSet<Team> teams = new HashSet<>();
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
    private final Scoreboard scoreboard;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;
    /**
     * A Set that stores teams who have used their teleports.
     */
    public final Set<Team> teamTpUsed = new HashSet<>();
    /**
     * A Set that stores players who have used their teleports.
     */
    public final Set<Player> playerTpUsed = new HashSet<>();
    /**
     * A HashMap that stores the amount of times a player or a team has teleported.
     */
    private final HashMap<Object, Integer> tpUsageCounter = new HashMap<>();
    private final HashMap<Team, Integer> teamPointsMap = new HashMap<>();
    private boolean showScoreboard = false;
    private final Map<String, Team> playersThatLeft = new HashMap<>();

    public void setShowScoreboard(boolean showScoreboard) {
        this.showScoreboard = showScoreboard;
        if (showScoreboard) {
            showScoreboard();
        } else {
            hideScoreboard();
        }
    }

    public TeamsManager(YamlConfiguration settings, GSBlockShuffle plugin) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.settings = settings;
        this.plugin = plugin;
    }

    public void handleRemainingPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerInNoTeam(player)) {
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
        if (team.getEntries().isEmpty()) {
            teamCaptains.put(player, team);
            if (messagePlayer) {
                plugin.sendMessage(player, "You are the captain of team " + team.getDisplayName());
            }
        }

        team.addEntry(player.getName());
    }

    public void joinTeamRequest(Player player, Team team) {
        teamRequests.put(getTeamCaptain(team), player);
        plugin.sendMessage(player, "You have requested to join team " + team.getDisplayName());

        //send message to team captain about the request
        TextComponent message = new TextComponent(player.getName() + " has requested to join your team.");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + " [Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle team accept"));
        message.addExtra(accept);

        plugin.sendMessage(getTeamCaptain(team), message);
    }

    public boolean joinTeamRequestAccept(Player captain) {
        Player player = teamRequests.get(captain);
        if (player == null || teamCaptains.get(captain) == null) {
            return false;
        }
        Team team = teamCaptains.get(captain);
        addPlayerToTeam(player, team, true);
        teamRequests.remove(captain);

        plugin.sendMessage(player, "You have been added to team " + team.getDisplayName());
        plugin.sendMessage(captain, player.getDisplayName() + " has been added to your team.");

        return true;
    }

    /**
     * Sends a team invite from the senderCaptain (captain) to the targetPlayer player.
     * If the targetPlayer player is already in a team or the senderCaptain is not a team captain, the method returns false.
     * If the invite is successfully sent, the method returns true.
     *
     * @param senderCaptain The player who is sending the invite.
     * @param targetPlayer  The player who is receiving the invite.
     * @return boolean Returns true if the invite is successfully sent, false otherwise.
     */
    public boolean teamInviteRequest(Player senderCaptain, Player targetPlayer) {
        if (getTeam(targetPlayer) != null) {
            return false;
        }
        if (!teamCaptains.containsKey(senderCaptain)) {
            return false;
        }
        plugin.sendMessage(senderCaptain, "You have invited " + targetPlayer.getName() + " to your team.");

        TextComponent message = new TextComponent("You have received an invite from " + senderCaptain.getName() + " to join their team.");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + " [Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle team accept"));
        message.addExtra(accept);

        plugin.sendMessage(targetPlayer, message);
        teamRequests.put(targetPlayer, senderCaptain);
        return true;
    }

    /**
     * Accepts a pending team invite for the senderPlayer (player).
     * If the senderPlayer does not have any pending team invites, the method returns false.
     * If the invite is successfully accepted, the senderPlayer is added to the team, and the invite is removed from the pending invites.
     *
     * @param senderPlayer The player who is accepting the invite.
     * @return boolean Returns true if the invite is successfully accepted, false otherwise.
     */
    public boolean teamInviteRequestAccept(Player senderPlayer) {
        if (teamRequests.containsKey(senderPlayer)) {
            Player captain = teamRequests.get(senderPlayer);
            Team team = getTeam(captain);
            addPlayerToTeam(senderPlayer, team, true);
            teamRequests.remove(senderPlayer);
            plugin.sendMessage(senderPlayer, "You have been added to team " + team.getDisplayName());
            return true;
        }
        return false;
    }

    /**
     * This method handles the teleport request between two players in the same team.
     * The tpRequester is the player who initiates the teleport request, and the tpTarget is the player who receives the request.
     * If either the tpRequester or the tpTarget is not in a team, or they are not in the same team, the method will return false.
     * If the teleport request is successfully sent, the method will return true.
     *
     * @param tpRequester The player who is sending the teleport request.
     * @param tpTarget    The player who is receiving the teleport request.
     * @return boolean – True if the teleport returns true if the request passed or an error message was sent to a player, false otherwise.
     */
    public boolean teamTeleportRequest(Player tpRequester, Player tpTarget) {
        Team senderTeam = getTeam(tpRequester);
        Team targetTeam = getTeam(tpTarget);

        String teleportMode = settings.getString("teleportMode");

        boolean teleportModeDisabled = Objects.equals(teleportMode, "disabled");
        boolean requesterAndTargetAreTheSame = tpRequester == tpTarget;
        boolean eitherTeamIsNull = senderTeam == null || targetTeam == null;

        if (teleportModeDisabled) {
            tpRequester.sendMessage(ChatColor.RED + "Teleporting is disabled.");
            return true;
        }
        if (requesterAndTargetAreTheSame) {
            tpRequester.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
            return true;
        }
        if (eitherTeamIsNull) {
            tpRequester.sendMessage(ChatColor.RED + "You or the target are not in a team.");
            return true;
        }

        if (Objects.equals(teleportMode, "amountPerTeam")) {
            // if team used up their teleports cancel the tp
            if (teamTpUsed.contains(getTeam(tpRequester))) {
                tpRequester.sendMessage(ChatColor.RED + "Your team has 0 teleports left.");
                return true;
            }
        }
        if (Objects.equals(teleportMode, "amountPerPlayer")) {
            // if team used up their teleports cancel the tp
            if (playerTpUsed.contains(tpRequester)) {
                tpRequester.sendMessage(ChatColor.RED + "You have 0 teleports left.");
                return true;
            }
        }

        if (senderTeam.equals(targetTeam)) {
            tpRequester.sendMessage(ChatColor.GREEN + "Teleport request sent to: " + tpTarget.getName());

            TextComponent message = new TextComponent("You have received a teleport request from: " + tpRequester.getName());
            TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + " [Accept]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle tpaccept"));
            message.addExtra(accept);

            plugin.sendMessage(tpTarget, message);

            teleportRequests.put(tpTarget, tpRequester);
            return true;
        }
        return false;
    }

    /**
     * This method handles the acceptance of a teleport request by a player.
     * The tpTarget is the player who is accepting the teleport request.
     * If the tpTarget does not have any pending teleport requests, the method will return false.
     * If the teleport request is successfully accepted, the method will handle the teleportation of the players and remove the request from the pending requests.
     *
     * @param tpTarget The player who is accepting the teleport request.
     * @return boolean Returns true if the teleport request is successfully accepted, false otherwise.
     */
    public boolean teamTeleportAccept(Player tpTarget) {
        if (teleportRequests.containsKey(tpTarget)) {
            Player tpRequester = teleportRequests.get(tpTarget);

            String message = ChatColor.GREEN + "Teleport request accepted.";

            tpTarget.sendMessage(message);
            tpRequester.sendMessage(message);

            handleTeamTeleport(tpRequester, tpTarget);
            teleportRequests.remove(tpTarget);

            return true;
        }
        return false;
    }

    public void handleTeamTeleport(Player tpRequester, Player tpTarget) {
        String teleportMode = settings.getString("teleportMode");
        int amountOfTeleports = settings.getInt("amountOfTeleports");

        if (Objects.equals(teleportMode, "disabled")) {
            return;
        }
        if (Objects.equals(teleportMode, "amountPerTeam")) {
            tpRequester.teleport(tpTarget);
            // if team not already in usage counter - add it
            if (!tpUsageCounter.containsKey(getTeam(tpRequester))) {
                tpUsageCounter.put(getTeam(tpRequester), 0);
            }
            // increment usage counter
            int tpsUsed = tpUsageCounter.get(getTeam(tpRequester));
            // increment tp counter and check if team reached their limit
            tpUsageCounter.put(getTeam(tpRequester), tpsUsed + 1);

            // send message to all players in the team
            for (String playerName : getTeam(tpRequester).getEntries()) {
                plugin.sendMessage(Objects.requireNonNull(Bukkit.getPlayer(playerName)),
                        tpRequester.getName() + ChatColor.GRAY + " has used teleport!\n" +
                                " Your team has " + ChatColor.WHITE + (amountOfTeleports - tpsUsed) + ChatColor.GRAY + " teleports left.");
            }

            if (tpsUsed >= amountOfTeleports) {
                teamTpUsed.add(getTeam(tpRequester));
            }
            return;
        }
        if (Objects.equals(teleportMode, "amountPerPlayer")) {
            tpRequester.teleport(tpTarget);
            // if team not already in usage counter - add it
            if (!tpUsageCounter.containsKey(tpRequester)) {
                tpUsageCounter.put(tpRequester, 0);
            }
            // increment usage counter
            int tpsUsed = tpUsageCounter.get(tpRequester);
            // increment tp counter and check if team reached their limit
            tpsUsed++;
            plugin.sendMessage(tpRequester, ChatColor.GRAY + "You have " + ChatColor.WHITE + (amountOfTeleports - tpsUsed) + ChatColor.GRAY + " teleports left.");
            tpUsageCounter.put(tpRequester, tpsUsed);
            if (tpsUsed >= amountOfTeleports) {
                playerTpUsed.add(tpRequester);
            }

            return;
        }
        if (Objects.equals(teleportMode, "unlimited")) {
            tpRequester.teleport(tpTarget);
            return;
        }

        plugin.sendMessage(tpRequester, "Invalid teleport mode. Please check the settings file.");
    }

    public void removePlayerFromTeam(Player player, Team team) {
        team.removeEntry(player.getName());
    }

    public void removePlayerFromTeamAfterLeave(Player player) {
        Team team = getTeam(player);
        team.removeEntry(player.getName());
        this.playersThatLeft.put(player.getName(), team);
    }

    public void reAddPlayerToTeamAfterLeave(Player player) {
        System.out.println("Adding player back to team: " + playersThatLeft);
        this.playersThatLeft.get(player.getName()).addEntry(player.getName());
        this.playersThatLeft.remove(player.getName());
    }

    /**
     * Handles the complete removal of a team from the game.
     *
     * @param team The team to be removed.
     */
    public void removeTeam(Team team) {
        Player captain = getTeamCaptain(team);
        teams.remove(team);
        teamCaptains.remove(captain);
        teamRequests.remove(captain);

        team.unregister();
    }

    public boolean isPlayerInNoTeam(Player player) {
        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return false;
            }
        }
        return true;
    }

    private void setTeamScore(Team team, int score) {
        teamPointsMap.put(team, score);
    }

    public void incrementTeamScore(Team team) {
        setTeamScore(team, getTeamScore(team) + 1);
        setScoreboard();
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

    public void setScoreboard() {
        scoreboard.getObjectives().forEach(Objective::unregister);

        Objective objective = scoreboard.registerNewObjective("Score", "dummy", "Score");

        for (Team team : teams) {
            Score score = objective.getScore(team.getDisplayName());
            score.setScore(teamPointsMap.getOrDefault(team, 0));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
        setShowScoreboard(showScoreboard);
    }

    public int getTeamScore(Team team) {
        return Objects.requireNonNull(scoreboard.getObjective("Score")).getScore(team.getDisplayName()).getScore();
    }

    public void clearScoreboards() {
        Objective objective = scoreboard.getObjective("Score");
        if (objective != null) {
            objective.unregister();
        }
        teamPointsMap.clear();
    }

    public List<Map.Entry<Team, Integer>> getTeamScoreList() {
        List<Map.Entry<Team, Integer>> list = new ArrayList<>(teamPointsMap.entrySet());

        System.out.println(teamPointsMap);

        list.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        return list;
    }

    public List<Map.Entry<Team, Integer>> getTeamPlaceList() {
        List<Map.Entry<Team, Integer>> sortedTeams = getTeamScoreList();
        List<Map.Entry<Team, Integer>> teamPlaceList = new ArrayList<>();

        int rank = 1;
        int prevScore = -1;

        for (int i = 0; i < sortedTeams.size(); i++) {
            int currentScore = sortedTeams.get(i).getValue();
            if (currentScore != prevScore) {
                rank = i + 1;
            }
            teamPlaceList.add(new AbstractMap.SimpleEntry<>(sortedTeams.get(i).getKey(), rank));
            prevScore = currentScore;
        }

        return teamPlaceList;
    }

    public Player getTeamCaptain(Team teamToSearch) {
        for (Player captain : teamCaptains.keySet()) {
            if (teamCaptains.get(captain).equals(teamToSearch)) {
                return captain;
            }
        }
        return null;
    }

    /**
     * Method handling leaving a team.
     * If the team will remain empty, the team will be removed.
     * If the captain leaves, another player will be made the captain.
     *
     * @param sender The player who is leaving the team.
     */
    public void leaveTeam(Player sender) {
        Team team = getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a team.");
            return;
        }
        removePlayerFromTeam(sender, team);
        if (team.getEntries().isEmpty()) {
            removeTeam(team);
        }
        if (teamCaptains.containsKey(sender)) {
            teamCaptains.remove(sender);
            // make another player the captain
            Player newCaptain = Bukkit.getPlayer(team.getEntries().iterator().next());
            teamCaptains.put(newCaptain, team);
            assert newCaptain != null;
            newCaptain.sendMessage("You are now the captain of " + team.getDisplayName());
        }
        sender.sendMessage(ChatColor.GREEN + "You have left your team.");
    }

    public void setTeamColor(Player sender, ChatColor color) {
        Team team = getTeam(sender);
        team.setColor(color);
        team.setPrefix(color.toString());
        updateTeamColor(team);
    }

    /**
     * Sets the display name of the team and the display names of its members to the teams color.
     *
     * @param team The team whose display name and the display names of its members are to be updated.
     */
    private void updateTeamColor(Team team) {
        team.setDisplayName(team.getColor() + team.getName());
        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                player.setDisplayName(team.getColor() + player.getName() + ChatColor.RESET);
                player.setScoreboard(scoreboard);
            }
        }
        setScoreboard();
    }

    public void showScoreboard() {
        Objects.requireNonNull(scoreboard.getObjective("Score")).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void hideScoreboard() {
        Objects.requireNonNull(scoreboard.getObjective("Score")).setDisplaySlot(null);
    }

    public void initializeTeamPointsMap() {
        for (Team team : teams) {
            teamPointsMap.put(team, 0);
        }
    }
}