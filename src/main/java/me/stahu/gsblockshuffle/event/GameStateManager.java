package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The GameStateManager class is used to manage and track the game state.
 * Tracks: teams / players and their assigned blocks, the current game state, and the game timer.
 */
public class GameStateManager {
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    public Map<String, Team> teams;
    public Map<String, ArrayList<String>> playerBlockMap;
    public int gameState; // 0 - not started, 1 - solo, 2 - team
    public ArrayList<Player> playersWithATeam;
    private GSBlockShuffle plugin;
    private YamlConfiguration settings;


    public GameStateManager(YamlConfiguration settings, GSBlockShuffle plugin) {
        this.teams = new HashMap<>();

        this.playerBlockMap = new HashMap<>();

        this.gameState = 0;

        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();

        this.settings = settings;
        this.plugin = plugin;
    }

    public void setGameState(int gameState) {
        if (gameState == 0) {
            // ..
        }
        if (gameState == 1) {
            // Adding players with no team to their own team
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!playersWithATeam.contains(player)) {
                    addTeam(player.getName(), ChatColor.WHITE);
                    addPlayerToTeam(player.getName(), player.getName());
                }
            }

            assignRandomBlocks();
        }
    }

    public int getGameState() {
        return gameState;
    }

    public void addTeam(String teamName, ChatColor teamColor) {
        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(teamColor);
        teams.put(teamName, team);
    }

    public void addPlayerToTeam(String teamName, String playerName) {
        Team team = teams.get(teamName);
        team.addEntry(playerName);
        playersWithATeam.add(Bukkit.getPlayer(playerName));
    }

    public void removePlayerFromAllTeams(String playerName) {
        for (Team team : teams.values()) {
            team.removeEntry(playerName);
        }
    }

    private void assignBlockToPlayer(String playerName, ArrayList<String> blockNameList) {
        System.out.println("Assigning block to player: " + playerName);
        playerBlockMap.put(playerName, blockNameList);
    }

    public void clearPlayerBlocks() {
        playerBlockMap.clear();
    }

    public void clearTeams() {
        for (Team team : teams.values()) {
            team.unregister();
        }
        teams.clear();
    }

    public void clearGameState() {
        clearPlayerBlocks();
        clearTeams();
        setGameState(0);
    }

    private void assignRandomBlocks() {
        String assignmentMode = settings.getString("assignmentMode");

        if (assignmentMode == "onePerPlayer") {
            ArrayList<String> blockName;
            for (Player player : Bukkit.getOnlinePlayers()) {
                blockName = plugin.categoryTree.getRandomBlock(settings);
                assignBlockToPlayer(player.getName(), blockName);
            }
        }

        if (assignmentMode == "onePerTeam") {
            ArrayList<String> blockName;
            for(Team team : teams.values()) {
                blockName = plugin.categoryTree.getRandomBlock(settings);
                for (String player : team.getEntries()) {
                    assignBlockToPlayer(player, blockName);
                }
            }
        }

        if(assignmentMode == "onePerRound") {
            ArrayList<String> blockName;
            blockName = plugin.categoryTree.getRandomBlock(settings);
            for (Player player : Bukkit.getOnlinePlayers()) {
                assignBlockToPlayer(player.getName(), blockName);
            }
        }
    }
}
