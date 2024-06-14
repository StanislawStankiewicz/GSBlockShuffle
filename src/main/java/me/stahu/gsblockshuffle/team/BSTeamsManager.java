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

public class BSTeamsManager implements TeamsManager {
    private final GSBlockShuffle plugin;
    private final YamlConfiguration settings;

    private final TreeSet<BSTeam> teams;
    private final Map<BSTeam, Player> teamJoinRequests;
    private final Map<Player, BSTeam> teamInviteRequests;

    private final Map<Player, Player> teleportRequests;
    private final Map<Object, Integer> tpUsageCounter;

    private final ScoreboardManager scoreboardManager;
    private final Scoreboard scoreboard;
    boolean showScoreboard;

    public TreeSet<BSTeam> getTeams() {
        return teams;
    }

    public void setShowScoreboard(boolean showScoreboard) {
        this.showScoreboard = showScoreboard;
        if (showScoreboard) {
            showScoreboard();
        } else {
            hideScoreboard();
        }
    }

    public BSTeamsManager(GSBlockShuffle plugin, YamlConfiguration settings) {
        this.plugin = plugin;
        this.settings = settings;

        this.teams = new TreeSet<>(new BSTeam.ScoreComparator());
        this.teamJoinRequests = new HashMap<>();
        this.teamInviteRequests = new HashMap<>();

        this.teleportRequests = new HashMap<>();
        this.tpUsageCounter = new HashMap<>();

        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = Objects.requireNonNull(scoreboardManager).getNewScoreboard();
        BSTeam.scoreboard = this.scoreboard;
        BSTeam.teamsManager = this;
    }

    @Override
    public BSTeam createTeam(String teamName, ChatColor teamColor) {
        BSTeam team = new BSTeam(teamName);
        teams.add(team);
        return team;
    }

    @Override
    public void removeTeam(BSTeam team) {
        teams.remove(team);
    }

    // TODO check if player in team
    public void leaveTeam(Player sender) {
        BSTeam team = getTeam(sender);
        removePlayerFromTeam(sender, team);
        sender.sendMessage(ChatColor.GREEN + "You have left your team.");
        if (team.getPlayers().isEmpty()) {
            removeTeam(team);
        }
        if (team.captain.equals(sender)) {
            // make another player the captain
            Player newCaptain = team.getPlayers().iterator().next();
            team.captain = newCaptain;
            newCaptain.sendMessage("You are now the captain of " + team.getDisplayName());
        }
    }

    @Override
    public void removePlayerFromTeam(Player player, BSTeam team) {
        team.removePlayer(player);
        if (team.getPlayers().isEmpty()) {
            removeTeam(team);
            return;
        }
        if (team.captain.equals(player)) {
            team.captain = team.getPlayers().iterator().next();
        }
    }

    @Override
    public void teamJoinRequest(Player senderPlayer, BSTeam team) {
        plugin.sendMessage(senderPlayer, "You have requested to join team " + team.getDisplayName());

        TextComponent message = new TextComponent(senderPlayer.getName() + " has requested to join your team. ");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle team accept"));
        message.addExtra(accept);
        plugin.sendMessage(team.captain, message);

        teamJoinRequests.put(team, senderPlayer);
    }

    @Override
    public boolean teamJoinAccept(Player senderCaptain) {
        BSTeam team = getTeam(senderCaptain);
        Player player = teamJoinRequests.get(team);

        if (player == null) {
            return false;
        }
        plugin.sendMessage(player, "You have been added to team " + team.getDisplayName());
        plugin.sendMessage(senderCaptain, player.getDisplayName() + " has been added to your team.");

        team.addPlayer(player);
        teamJoinRequests.remove(team);
        return true;
    }

    @Override
    public void teamInvite(Player senderCaptain, Player targetPlayer) {
        plugin.sendMessage(senderCaptain, "You have invited " + targetPlayer.getName() + " to your team.");

        TextComponent message = new TextComponent("You have received an invite from " + senderCaptain.getName() + " to join their team. ");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle team accept"));
        message.addExtra(accept);
        plugin.sendMessage(targetPlayer, message);

        teamInviteRequests.put(targetPlayer, getTeam(senderCaptain));
    }

    @Override
    public boolean teamInviteAccept(Player senderPlayer) {
        BSTeam team = teamInviteRequests.get(senderPlayer);
        if (team == null) {
            return false;
        }
        plugin.sendMessage(senderPlayer, "You have been added to team " + team.getDisplayName());

        team.addPlayer(senderPlayer);
        teamInviteRequests.remove(senderPlayer);
        return true;
    }

    @Override
    public BSTeam getTeam(String teamName) {
        for (BSTeam team : teams) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }
        return null;
    }

    @Override
    public BSTeam getTeam(Player player) {
        for (BSTeam team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    // TODO if (senderTeam.equals(targetTeam)) <- this check in commands
    @Override
    public void teamTeleportRequest(Player tpRequester, Player tpTarget) {
        if (!checkTeleportSettings(tpRequester, tpTarget)) {
            return;
        }
        tpRequester.sendMessage(ChatColor.GREEN + "Teleport request sent to: " + tpTarget.getName());

        TextComponent message = new TextComponent("You have received a teleport request from: " + tpRequester.getName());
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + " [Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gsblockshuffle tpaccept"));
        message.addExtra(accept);

        plugin.sendMessage(tpTarget, message);

        teleportRequests.put(tpTarget, tpRequester);
    }

    private boolean checkTeleportSettings(Player tpRequester, Player tpTarget) {
        BSTeam requesterTeam = getTeam(tpRequester);
        BSTeam targetTeam = getTeam(tpTarget);

        String teleportMode = settings.getString("teleportMode");
        int teleportsPerRound = settings.getInt("teleportsPerRound");

        boolean teleportModeDisabled = Objects.equals(teleportMode, "disabled");
        boolean requesterAndTargetAreTheSame = tpRequester == tpTarget;
        boolean eitherTeamIsNull = requesterTeam == null || targetTeam == null;

        if (teleportModeDisabled) {
            tpRequester.sendMessage(ChatColor.RED + "Teleporting is disabled.");
            return false;
        }
        if (requesterAndTargetAreTheSame) {
            tpRequester.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
            return false;
        }
        if (eitherTeamIsNull) {
            tpRequester.sendMessage(ChatColor.RED + "You or the target are not in a team.");
            return false;
        }
        // TODO refactor this
        if (Objects.equals(teleportMode, "amountPerTeam")) {
            if (tpUsageCounter.get(requesterTeam) >= teleportsPerRound) {
                tpRequester.sendMessage(ChatColor.RED + "Your team has 0 teleports left.");
                return false;
            }
        }
        if (Objects.equals(teleportMode, "amountPerPlayer")) {
            if (tpUsageCounter.get(requesterTeam) >= teleportsPerRound) {
                tpRequester.sendMessage(ChatColor.RED + "You have 0 teleports left.");
                return false;
            }
        }
        return true;
    }

    @Override
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

    @Override
    public void clearTeleports() {
        tpUsageCounter.clear();
    }

    // TODO refactor
    private void handleTeamTeleport(Player tpRequester, Player tpTarget) {
        String teleportMode = settings.getString("teleportMode");
        int amountOfTeleports = settings.getInt("amountOfTeleports");

        if (Objects.equals(teleportMode, "disabled")) {
            return;
        }
        if (Objects.equals(teleportMode, "amountPerTeam")) {
            tpRequester.teleport(tpTarget);
            if (!tpUsageCounter.containsKey(getTeam(tpRequester))) {
                tpUsageCounter.put(getTeam(tpRequester), 0);
            }
            int tpsUsed = tpUsageCounter.get(getTeam(tpRequester));
            tpUsageCounter.put(getTeam(tpRequester), tpsUsed + 1);

            for (Player player : getTeam(tpRequester).getPlayers()) {
                plugin.sendMessage(player, tpRequester.getName() + ChatColor.GRAY + " has used teleport!\n" + " Your team has " + ChatColor.WHITE + (amountOfTeleports - tpsUsed) + ChatColor.GRAY + " teleports left.");
            }
            return;
        }
        if (Objects.equals(teleportMode, "amountPerPlayer")) {
            tpRequester.teleport(tpTarget);
            if (!tpUsageCounter.containsKey(tpRequester)) {
                tpUsageCounter.put(tpRequester, 0);
            }
            int tpsUsed = tpUsageCounter.get(tpRequester);
            tpsUsed++;
            plugin.sendMessage(tpRequester, ChatColor.GRAY + "You have " + ChatColor.WHITE + (amountOfTeleports - tpsUsed) + ChatColor.GRAY + " teleports left.");
            tpUsageCounter.put(tpRequester, tpsUsed);
            return;
        }
        if (Objects.equals(teleportMode, "unlimited")) {
            tpRequester.teleport(tpTarget);
            return;
        }

        plugin.sendMessage(tpRequester, "Invalid teleport mode. Please check the settings file.");
    }

    public void setScoreboard() {
        scoreboard.getObjectives().forEach(Objective::unregister);

        Objective objective = scoreboard.registerNewObjective("Score", "dummy", "Score");

        for (BSTeam team : teams) {
            Score score = objective.getScore(team.getDisplayName());
            score.setScore(team.getScore());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
        setShowScoreboard(showScoreboard);
    }

    public void showScoreboard() {
        Objects.requireNonNull(scoreboard.getObjective("Score")).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void hideScoreboard() {
        Objects.requireNonNull(scoreboard.getObjective("Score")).setDisplaySlot(null);
    }

    public void clearScoreboards() {
        Objective objective = scoreboard.getObjective("Score");
        if (objective != null) {
            objective.unregister();
        }
        for (BSTeam team : teams) {
            team.resetScore();
        }
    }

    public void incrementTeamScore(BSTeam team) {
        team.incScore();
        // update scoreboard as it is not updated automatically
        setScoreboard();
    }

    public ArrayList<BSTeam> getWinningTeams() {
        ArrayList<BSTeam> winningTeams = new ArrayList<>();

        for (BSTeam team : getTeams()) {
            if (winningTeams.isEmpty() && !team.isEliminated) {
                winningTeams.add(team);
            } else if (team.getScore() == winningTeams.get(0).getScore() && !team.isEliminated) {
                winningTeams.add(team);
            }
        }
        return winningTeams;
    }
}
