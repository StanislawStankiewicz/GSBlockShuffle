package me.stahu.gsblockshuffle.team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TeamsManager {
    TreeSet<BSTeam> getTeams();

    void setShowScoreboard(boolean showScoreboard);

    BSTeam createTeam(String teamName, ChatColor teamColor);

    void removeTeam(BSTeam team);

    void leaveTeam(Player player);

    void removePlayerFromTeam(Player player, BSTeam team);

    void teamJoinRequest(Player sender, BSTeam team);

    boolean teamJoinAccept(Player sender);

    void teamInvite(Player sender, Player target);

    boolean teamInviteAccept(Player sender);

    // TODO return optional instead of null
    BSTeam getTeam(String teamName);

    // TODO refactor getTeam calls
    BSTeam getTeam(Player player);

    void teamTeleportRequest(Player sender, Player target);

    boolean teamTeleportAccept(Player sender);

    void clearTeleports();

    void setScoreboard();

    void showScoreboard();

    void clearScoreboards();

    void incrementTeamScore(BSTeam team);

    ArrayList<BSTeam> getWinningTeams();
}
