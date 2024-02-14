package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.random_block.RandomBlock;
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
    private GSBlockShuffle plugin;
    private YamlConfiguration settings;

    public GameStateManager(YamlConfiguration settings,GSBlockShuffle plugin) {
        this.teams = new HashMap<>();

        this.playerBlockMap = new HashMap<>();

        this.gameState = 0;

        this.scoreboardManager =  Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();

        this.settings = settings;
        this.plugin = plugin;
    }

    public void setGameState(int gameState) {
        System.out.println("Setting game state to: " + gameState);
        System.out.println("gamestate == 1: " + (gameState == 1));
        this.gameState = gameState;
        if (gameState == 1) {
            clearPlayerBlocks();
            assignRandomBlocks();
            System.out.println("Player blocks: " + playerBlockMap);
            Objective objective = scoreboard.registerNewObjective("BlockShuffle", "dummy", ChatColor.GOLD + "Block Shuffle", RenderType.INTEGER);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        if (gameState == 2) {
            //...
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
        System.out.println("Assigning random blocks");
        System.out.println("Game state: " + gameState);
        ArrayList<ArrayList<ArrayList<String>>> blocks = plugin.categoryTree.getBlockList(settings);


        if (gameState == 2) {
            for (Team team : teams.values()) {
                //...
            }
            // TODO implement
        }
        if (gameState == 1) {
            // get all players
            ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            System.out.println("players: " + players);
            for (Player player : players) {
                // assign block to player
                assignBlockToPlayer(player.getName(), RandomBlock.getRandomBlock(blocks));
                player.sendRawMessage("Your block is: " + playerBlockMap.get(player.getName()));
            }
        }
    }
}
