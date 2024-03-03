package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.BossBarTimer;
import me.stahu.gsblockshuffle.team.TeamsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

import static me.stahu.gsblockshuffle.sound.Sounds.*;
import static me.stahu.gsblockshuffle.gui.SplashTitle.*;


public class GameStateManager {
    private final GSBlockShuffle plugin;
    private final YamlConfiguration settings;
    private final TeamsManager teamsManager;
    private int gameState; // 0 - not started, 1 - started
    private int roundsRemaining;
    private int currentRound = 0;
    public int secondsInRoundBreak;
    private int secondsLeft;
    private int roundTickTask;
    private int roundBreakTickTask;
    private int roundStartTask;
    public BossBarTimer bossBarTimer;
    public Map<String, ArrayList<String>> playerBlockMap;
    public HashSet<Player> playersWithFoundBlock = new HashSet<>();

    public boolean setGameState(int gameState) {
        if (this.gameState == gameState) {
            return false;
        }
        if (gameState == 0) {
            this.gameState = gameState;
            endGame();
            return true;
        }
        if (gameState == 1) {
            this.gameState = gameState;
            startGame();
            return true;
        }
        return false;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    public GameStateManager(YamlConfiguration settings, GSBlockShuffle plugin, TeamsManager teamsManager, BossBarTimer bossBarTimer) {
        this.teamsManager = teamsManager;
        this.bossBarTimer = bossBarTimer;

        this.playerBlockMap = new HashMap<>();

        this.gameState = 0;

        this.settings = settings;
        this.plugin = plugin;
    }

    public void startGame() {
        // Adding players with no team to their own team
        teamsManager.handleRemainingPlayers();

        teamsManager.setScoreboard();
        teamsManager.setShowScoreboard(true);
        teamsManager.initializeTeamPointsMap();

        roundsRemaining = settings.getInt("roundsPerGame");

        playRoundCountdownSound(plugin, settings, teamsManager);

        roundStartTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::newRound, 40);
    }

    public void newRound() {
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);
        playerBlockMap.clear();
        playersWithFoundBlock.clear();
        teamsManager.playerTpUsed.clear();
        teamsManager.teamTpUsed.clear();
        bossBarTimer.clearBossBars();

        //create compass based on seting "showTeamCompass"
        if (settings.getBoolean("showTeamCompass")) {
            plugin.teammateCompass.createCompassBars();
        } else {
            plugin.teammateCompass.clearCompassBars();
        }

        assignRandomBlocks();

        currentRound++;
        roundsRemaining--;
        bossBarTimer.createBossBar();
        secondsLeft = settings.getInt("roundTime");
        roundTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundTick, 0, 20);
    }

    private void roundTick() {
        if (secondsLeft-- <= 0) {
            endRound();
            return;
        }
        double progress = secondsLeft / (double) (settings.getInt("roundTime"));

        bossBarTimer.updateBossBar(progress, secondsLeft);
        //display actionbar title for everyone with a block
        for (Player player : teamsManager.getPlayersWithATeam()) {
            if (playerBlockMap.get(player.getName()) != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + playerBlockMap.get(player.getName()).get(0).replace("_", " ")));
            }
        }
        if (secondsLeft < 61) {
            pingPlayers(plugin, settings, secondsLeft);
        }
    }

    public void endRound() {
        Bukkit.getScheduler().cancelTask(this.roundTickTask);
        HashSet<Team> teamsToEliminate = getTeamsToEliminate();

        // block not found sound
        for (Team team : teamsManager.teams) {
            for (String playerName : team.getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                if (!playersWithFoundBlock.contains(player) && !isGameEnding(false)
                        && !settings.getBoolean("eliminateAfterRound")) {
                    playBlockFoundSound(plugin, settings, player, false);
                }
            }
        }

        // eliminate teams
        for (Team team : teamsToEliminate) {
            if (!(isGameEnding(false) && teamsManager.isTeamWinning(team))) {
                for (String playerName : team.getEntries()) {
                    Player player = Bukkit.getPlayer(playerName);
                    showEliminatedTitle(settings, player);
                    playEliminationSound(plugin, settings, player);
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.sendMessage(player, team.getDisplayName() + " has been eliminated!");
                }
            }

            teamsManager.eliminateTeam(team);
        }

        if (isGameEnding(true)) {
            setGameState(0);
            return;
        }

        playerBlockMap.clear();
        increaseDifficulty();

        roundBreak();
    }

    /**
     * Initiates the break between rounds.
     * This method sets the duration of the round break based on the "roundBreakTime" setting.
     * It then creates a new boss bar and schedules the roundBreakTick method to be called every tick (20 ticks = 1 second).
     */
    public void roundBreak() {
        secondsInRoundBreak = settings.getInt("roundBreakTime");
        secondsLeft = secondsInRoundBreak;
        bossBarTimer.clearBossBars();
        bossBarTimer.createBossBar();

        roundBreakTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundBreakTick, 0, 20);
    }

    /**
     * Handles the tick event during the break between rounds.
     * This method decreases the seconds left in the break by one each tick.
     * If the seconds left reach zero, the boss bar is removed and a new round is started.
     * If there is only one second left, a countdown sound is played.
     * The progress of the boss bar is updated based on the remaining time in the break.
     */
    private void roundBreakTick() {
        if (secondsLeft-- <= 0) {
            bossBarTimer.clearBossBars();
            newRound();
            return;
        }

        if (secondsLeft == 1) {
            playRoundCountdownSound(plugin, settings, teamsManager);
        }

        double progress = secondsLeft / (double) (secondsInRoundBreak);
        bossBarTimer.updateBreakBossBar(progress, secondsLeft);
    }

    // TODO delete temporary teams after game ends
    /**
     * Ends the game and sends a message to all online players.
     * This method constructs a message indicating the end of the game and the final scores of each team.
     * The message is then sent to all online players.
     * Note: The getTeamScore method is used to retrieve the score of each team.
     */
    public void endGame() {
        Bukkit.getScheduler().cancelTask(this.roundStartTask);
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);
        Bukkit.getScheduler().cancelTask(this.roundTickTask);

        List<Map.Entry<Team, Integer>> teamPlaceList = teamsManager.getTeamPlaceList();
        List<String> winningTeamNames = new ArrayList<>();

        // get winning teams
        for(Map.Entry<Team, Integer> entry : teamPlaceList) {
            if (entry.getValue() != 1) {
                break;
            }
            winningTeamNames.add(entry.getKey().getDisplayName());
        }

        // TODO resolve block not found and elimination sound conflict
        // show splash titles
        for (Map.Entry<Team, Integer> entry : teamPlaceList) {
            if (entry.getValue() == 1) {
                for (String playerName : entry.getKey().getEntries()) {
                    Player player = Bukkit.getPlayer(playerName);
                    showYouWonTitle(settings, player);
                    playWinnerSound(plugin, settings, player);
                }
            } else {
                for (String playerName : entry.getKey().getEntries()) {
                    Player player = Bukkit.getPlayer(playerName);
                    showPlayerWonTitle(settings, player, winningTeamNames);
                }
            }
        }

        sendEndGameMessageToAllPlayers(teamPlaceList);

        playerBlockMap.clear();
        plugin.teammateCompass.clearCompassBars();
        bossBarTimer.clearBossBars();
        teamsManager.clearScoreboards();
        teamsManager.resetEliminatedTeams();
    }

    /**
     * Sends the final team leaderboard to all players.
     * The positions in the leaderboard follow the ex aequo rule, which means that teams with the same score get the same place.
     * If two or more teams share a place, the next rank(s) is/are skipped.
     *
     * @param teamPlaceList A list of teams and their final scores, sorted by score in descending order.
     */
    private void sendEndGameMessageToAllPlayers(List<Map.Entry<Team, Integer>> teamPlaceList) {
        StringBuilder endMessage = new StringBuilder("Game ended!\n" + " Final scores:");

        for (Map.Entry<Team, Integer> entry : teamPlaceList) {
            Team team = entry.getKey();
            int rank = entry.getValue();
            int currentScore = teamsManager.getTeamScore(team);

            ChatColor rankColor = switch (rank) {
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.GRAY;
                case 3 -> ChatColor.RED;
                default -> ChatColor.DARK_GRAY;
            };
//            "\n i. TeamName: score"
            endMessage.append("\n ").append(rankColor).append(rank).append(ChatColor.WHITE).append(". ").append(team.getDisplayName()).append(": ").append(currentScore);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.sendMessage(player, endMessage.toString());
        }
    }

    /**
     * Assigns random blocks to players based on the block assignment mode.
     * The block assignment mode can be one of the following: "onePerPlayer", "onePerTeam", or "onePerRound".
     * If the block assignment mode is incorrectly set, an error message is printed and the game ends.
     * "onePerPlayer": each player in each team is assigned a random block.
     * "onePerTeam": each team is assigned a random block, and all players in the team are assigned the same block.
     * "onePerRound": a random block is assigned to all players in all teams.
     * After the block is assigned, the player is sent a message with the name of their block.
     */
    private void assignRandomBlocks() {
        String blockAssignmentMode = settings.getString("blockAssignmentMode");
        ArrayList<String> blockNames = null;
        String blockName;
        List<String> blockAssignmentModes;
        blockAssignmentModes = List.of("onePerPlayer", "onePerTeam", "onePerRound");

        if (!blockAssignmentModes.contains(blockAssignmentMode)) {
            // TODO raise error
            System.out.println("Error: blockAssignmentMode incorrectly set to: " + blockAssignmentMode);
            setGameState(0);
            return;
        }

        ArrayList<ArrayList<ArrayList<String>>> blockList = plugin.categoryTree.getBlockList(settings);

        if (Objects.equals(blockAssignmentMode, "onePerRound")) {
            blockNames = getRandomBlock(blockList);
        }
        for (Team team : teamsManager.teams) {
            if (Objects.equals(blockAssignmentMode, "onePerTeam")) {
                blockNames = getRandomBlock(blockList);
            }
            for (String playerName : team.getEntries()) {
                if (Objects.equals(blockAssignmentMode, "onePerPlayer")) {
                    blockNames = getRandomBlock(blockList);
                }
                assignBlockToPlayer(playerName, blockNames);
                Player player = Bukkit.getPlayer(playerName);
                assert blockNames != null;
                blockName = blockNames.get(0);
                blockName = blockName.replaceAll("_", " ");
                assert player != null;
                plugin.sendMessage(player, "Your block is: " + ChatColor.GOLD + blockName);
            }
        }
    }

    public ArrayList<String> getRandomBlock(ArrayList<ArrayList<ArrayList<String>>> blockList) {
        Random random = new Random();

        if (blockList.isEmpty()) {
            throw new IllegalArgumentException("Block list is empty");
        }

        ArrayList<ArrayList<String>> block = blockList.get(random.nextInt(blockList.size()));

        // Return random variant of the block
        return block.get(random.nextInt(block.size()));
    }

    /**
     * Assigns a block to a player.
     * This method adds the player's name and the list of block names to the playerBlockMap.
     *
     * @param playerName    The name of the player to whom the block will be assigned.
     * @param blockNameList The list of block names to be assigned to the player.
     */
    private void assignBlockToPlayer(String playerName, ArrayList<String> blockNameList) {
        playerBlockMap.put(playerName, blockNameList);
        System.out.println(playerName + " got " + blockNameList);
    }

    /**
     * Handles the event when a player has found a block.
     * This method retrieves the block assignment mode and various game settings from the configuration.
     * Depending on the block assignment mode, it updates the player's status, the team's score, and potentially ends the round or the game.
     *
     * @param player The player who has found a block.
     */
    public void playerFoundBlock(Player player) {
        boolean firstToWin = settings.getBoolean("firstToWin");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");
        boolean teamScoreIncrementPerPlayer = settings.getBoolean("teamScoreIncrementPerPlayer");
        boolean teamFoundBlock = false;

        Team team = teamsManager.getTeam(player);

        // if true just increment the teams score
        if (teamScoreIncrementPerPlayer) {
            teamsManager.incrementTeamScore(team);
        } else {
            // else check if teamscore already incremented
            for (String playerName : team.getEntries()) {
                Player p = Bukkit.getPlayer(playerName);
                if (playersWithFoundBlock.contains(p)) {
                    teamFoundBlock = true;
                    break;
                }
            }
            if (!teamFoundBlock) {
                teamsManager.incrementTeamScore(team);
            }
        }

        playersWithFoundBlock.add(player);

        // block found message
        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.sendMessage(p, player.getDisplayName() + " has found their block! (" + ChatColor.GOLD
                    + playerBlockMap.get(player.getName()).get(0).replace("_", " ") + ChatColor.RESET + ")");
        }

        playerBlockMap.remove(player.getName());

        // avoid sound collision with win sound if player is in winning team
        if (teamsManager.isTeamWinning(team) && !isGameEnding(false)) {
            playBlockFoundSound(plugin, settings, player, true);
        }

        // if firstToWin endRound
        if (firstToWin) {
            // if not allPlayersRequiredForTeamWin set teammates block to found
            if (!allPlayersRequiredForTeamWin) {
                for (String playerName : team.getEntries()) {
                    playersWithFoundBlock.add(Bukkit.getPlayer(playerName));
                    playerBlockMap.remove(playerName);
                }
                endRound();
                return;
            }
            for (String playerName : team.getEntries()) {
                if (!playersWithFoundBlock.contains(Bukkit.getPlayer(playerName))) {
                    return;
                }
            }
            endRound();
            return;
        }
        // if no players left - endRound
        if (playerBlockMap.isEmpty()) {
            endRound();
        }
    }

    private HashSet<Team> getTeamsToEliminate() {
        int membersWithoutBlock;
        HashSet<Team> eliminatedTeams = new HashSet<>();
        boolean eliminateAfterRound = settings.getBoolean("eliminateAfterRound");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");

        for (Team team : teamsManager.teams) {
            membersWithoutBlock = 0;
            for (String playerName : team.getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                if (!playersWithFoundBlock.contains(player)) {
                    // you did not find your block
                    if (eliminateAfterRound) {
                        membersWithoutBlock++;
                        eliminatedTeams.add(team);
                    }
                }
            }
            if (allPlayersRequiredForTeamWin && membersWithoutBlock > 0) {
                eliminatedTeams.add(team);
            }
        }
        return eliminatedTeams;
    }

    private void increaseDifficulty() {
        if (settings.getBoolean("increaseDifficulty")
                && (settings.getInt("difficulty") < Math.min(settings.getInt("difficultyCap"), 1000))) {
            if (settings.getInt("increaseEveryNRounds") != -1) {
                if (currentRound % settings.getInt("increaseEveryNRounds") == 0) {
                    incrementDifficulty();
                }
            } else {
                //custom increase
                List<Integer> customIncrease = settings.getIntegerList("customIncrease");
                if (customIncrease.contains(currentRound)) {
                    incrementDifficulty();
                }
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.sendMessage(player, "Difficulty: " + settings.getInt("difficulty"));
        }
    }

    public void handlePlayerMove(Player player) {
        if (gameState == 0) {
            return;
        }

        String playerName = player.getName();
        Location playerLocation = player.getLocation();

        if (teamsManager.isPlayerInNoTeam(player)) {
            return;
        }
        if (playerBlockMap.get(playerName) == null) {
            return;
        }

        for (String blockName : playerBlockMap.get(playerName)) {
            Material playerBlock = Material.getMaterial(blockName);
            if (playerBlock == playerLocation.getBlock().getType() || playerBlock == playerLocation.getBlock().getRelative(0, -1, 0).getType()) {
                playerFoundBlock(player);
            }
        }
    }

    // TODO optimize
    private void incrementDifficulty() {
        int previousDifficulty = settings.getInt("difficulty");

        settings.set("difficulty", settings.getInt("difficulty") + 1);

        ArrayList<ArrayList<ArrayList<String>>> blockList = plugin.categoryTree.getBlockList(settings);

        while (blockList.isEmpty()
                && (settings.getInt("difficulty") < Math.min(settings.getInt("difficultyCap"), 1000))) {
            settings.set("difficulty", settings.getInt("difficulty") + 1);
            blockList = plugin.categoryTree.getBlockList(settings);
        }

        if (blockList.isEmpty()) {
            settings.set("difficulty", previousDifficulty);
        }
    }

    private boolean isGameEnding(boolean teamsEliminated) {
        boolean endGameIfOneTeamRemaining = settings.getBoolean("endGameIfOneTeamRemaining");
        int teamsSizeAfterElimination = teamsManager.teams.size();

//        System.out.println("Teams eliminated: " + teamsEliminated);
//        System.out.println("Teams to eliminate: " + getTeamsToEliminate().size());

        if (!teamsEliminated) {
            teamsSizeAfterElimination -= getTeamsToEliminate().size();
        }
//        System.out.println("Teams size: " + teamsSizeAfterElimination);

        if (teamsSizeAfterElimination == 0) {
            return true;
        }
        if (endGameIfOneTeamRemaining && teamsSizeAfterElimination == 1) {
            return true;
        } else return roundsRemaining == 0;
    }
}
