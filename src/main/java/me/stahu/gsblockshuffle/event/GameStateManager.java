package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.BossBarTimer;
import me.stahu.gsblockshuffle.team.BSTeam;
import me.stahu.gsblockshuffle.team.TeamsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

import static me.stahu.gsblockshuffle.sound.Sounds.*;
import static me.stahu.gsblockshuffle.gui.SplashTitle.*;

public class GameStateManager {
    private final GSBlockShuffle plugin;
    private final YamlConfiguration settings;
    private final TeamsManager teamsManager;
    public BossBarTimer bossBarTimer;

    private GameState gameState;
    private int totalRounds;
    private int roundTimeSeconds;
    private int breakTimeSeconds;

    private int roundsRemaining;
    private int secondsLeft;

    private int roundStartTask;
    private int roundTickTask;
    private int roundBreakTickTask;
    private BAMode blockAssignmentMode;

    private final Map<Player, ArrayList<String>> playerBlockMap;
    private final Set<Player> playersWhoFoundBlock;

    public boolean setGameState(GameState gameState) {
        if (this.gameState == gameState) {
            return false;
        }
        if (gameState == GameState.IDLE) {
            this.gameState = gameState;
            endGame();
            return true;
        }
        if (gameState == GameState.RUNNING) {
            this.gameState = gameState;
            startGame();
            return true;
        }
        return false;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    public GameStateManager(GSBlockShuffle plugin, YamlConfiguration settings, TeamsManager teamsManager,
                            BossBarTimer bossBarTimer) {
        this.plugin = plugin;
        this.settings = settings;
        this.teamsManager = teamsManager;
        this.bossBarTimer = bossBarTimer;

        this.gameState = GameState.IDLE;
        updateSettings();

        this.playerBlockMap = new HashMap<>();
        this.playersWhoFoundBlock = new HashSet<>();
    }

    private void updateSettings() {
        this.totalRounds = settings.getInt("totalRounds");
        this.roundTimeSeconds = settings.getInt("roundTimeSeconds");
        this.breakTimeSeconds = settings.getInt("breakTimeSeconds");
        String blockAssignmentMode = Objects.requireNonNull(settings.getString("blockAssignmentMode"));
        this.blockAssignmentMode = BAMode.fromString(blockAssignmentMode);
    }

    public void startGame() {
        // handle remaining players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (teamsManager.getTeam(player) == null) {
                BSTeam team = teamsManager.createTeam(player.getName(), ChatColor.WHITE);
                team.addPlayer(player);
            }
        }
        teamsManager.setScoreboard();
        teamsManager.setShowScoreboard(true);

        roundsRemaining = settings.getInt("rounds");
        playRoundCountdownSound(plugin, settings, teamsManager);

        roundStartTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::newRound, 40);
    }

    public void newRound() {
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);
        teamsManager.clearTeleports();
        bossBarTimer.clearBossBars();
        playerBlockMap.clear();
        playersWhoFoundBlock.clear();

        if (settings.getBoolean("showTeamCompass")) {
            plugin.teammateCompass.createCompassBars();
        } else {
            plugin.teammateCompass.clearCompassBars();
        }

        assignBlocks();

        roundsRemaining--;
        bossBarTimer.createBossBar();
        secondsLeft = roundTimeSeconds;
        roundTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundTick, 0, 20);
    }

    private void roundTick() {
        if (secondsLeft-- <= 0) {
            endRound();
            return;
        }
        double progress = secondsLeft / (double) (settings.getInt("roundTimeSeconds"));
        bossBarTimer.updateBossBar(progress, secondsLeft);
        //display actionbar title for everyone with a block
        for (BSTeam team : teamsManager.getTeams()) {
            for (Player player : team.getPlayers()) {
                if (playerBlockMap.get(player) != null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD
                            + playerBlockMap.get(player).get(0).replace("_", " ")));
                }
            }
        }
        if (secondsLeft <= 60) {
            pingPlayers(plugin, settings, secondsLeft);
        }
    }

    public void endRound() {
        Bukkit.getScheduler().cancelTask(this.roundTickTask);
        HashSet<BSTeam> teamsToEliminate = getTeamsToEliminate();

        // TODO REFACTOR SOUNDS!!!
        // block not found sound
        for (BSTeam team : teamsManager.getTeams()) {
            for (Player player: team.getPlayers()) {
                if (!playersWhoFoundBlock.contains(player) && !isGameEnding(false)
                        && !settings.getBoolean("eliminateAfterRound")) {
                    playBlockFoundSound(plugin, settings, player, false);
                }
            }
        }
        // eliminate teams
        ArrayList<BSTeam> winningTeams = teamsManager.getWinningTeams();
        for (BSTeam team : teamsToEliminate) {
            if (!(isGameEnding(false) && winningTeams.contains(team))) {
                for (Player player: team.getPlayers()) {
                    showEliminatedTitle(settings, player);
                    playEliminationSound(plugin, settings, player);
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.sendMessage(player, team.getDisplayName() + " has been eliminated!");
                }
            }
            team.isEliminated = true;
        }

        if (isGameEnding(true)) {
            setGameState(GameState.IDLE);
            return;
        }

        playerBlockMap.clear();
        increaseDifficulty();

        roundBreak();
    }

    public void roundBreak() {
        bossBarTimer.clearBossBars();
        bossBarTimer.createBossBar();
        breakTimeSeconds = settings.getInt("roundBreakSeconds");
        secondsLeft = breakTimeSeconds;

        roundBreakTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundBreakTick, 0, 20);
    }

    public void roundBreakTick() {
        if (secondsLeft-- <= 0) {
            bossBarTimer.clearBossBars();
            newRound();
            return;
        }

        if (secondsLeft == 1) {
            playRoundCountdownSound(plugin, settings, teamsManager);
        }

        double progress = secondsLeft / (double) (breakTimeSeconds);
        bossBarTimer.updateBreakBossBar(progress, secondsLeft);
    }

    public void endGame() {
        Bukkit.getScheduler().cancelTask(this.roundStartTask);
        Bukkit.getScheduler().cancelTask(this.roundTickTask);
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);

        ArrayList<BSTeam> winningTeams = teamsManager.getWinningTeams();

        // TODO resolve block not found and elimination sound conflict
        // show splash titles
        for (BSTeam team : teamsManager.getTeams()) {
            if (winningTeams.contains(team)) {
                for (Player player : team.getPlayers()) {
                    showWinnerTitle(settings, player);
                    playWinnerSound(plugin, settings, player);
                }
            } else {
                for (Player player : team.getPlayers()) {
                    showPlayerWonTitle(settings, player, winningTeams);
                }
            }
        }

        sendEndGameMessageToAllPlayers();

        playerBlockMap.clear();
        plugin.teammateCompass.clearCompassBars();
        bossBarTimer.clearBossBars();
        teamsManager.clearScoreboards();
        for (BSTeam team : teamsManager.getTeams()) {
            team.resetScore();
            team.isEliminated = false;
        }
    }

    private HashSet<BSTeam> getTeamsToEliminate() {
        int membersWithoutBlock;
        HashSet<BSTeam> teamsToEliminate = new HashSet<>();
        boolean eliminateAfterRound = settings.getBoolean("eliminateAfterRound");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");

        for (BSTeam team : teamsManager.getTeams()) {
            membersWithoutBlock = 0;
            for (Player player : team.getPlayers()) {
                if (!playersWhoFoundBlock.contains(player)) {
                    if (eliminateAfterRound) {
                        membersWithoutBlock++;
                        teamsToEliminate.add(team);
                    }
                }
            }
            if (allPlayersRequiredForTeamWin && membersWithoutBlock > 0) {
                teamsToEliminate.add(team);
            }
        }
        return teamsToEliminate;
    }

    private void increaseDifficulty() {
        if (settings.getBoolean("increaseDifficulty")
                && (settings.getInt("difficulty") < Math.min(settings.getInt("difficultyCap"), 1000))) {
            int currentRound = totalRounds - roundsRemaining;
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

    // TODO is this really the way?
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

    private void sendEndGameMessageToAllPlayers() {
        StringBuilder endMessage = new StringBuilder("Game ended!\n" + " Final scores:");
        int currentPlace = 0;
        int previousScore = -1;

        for (BSTeam team : teamsManager.getTeams()) {
            if (team.getScore() != previousScore) {
                currentPlace++;
            }
            ChatColor rankColor = switch (currentPlace) {
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.GRAY;
                case 3 -> ChatColor.RED;
                default -> ChatColor.DARK_GRAY;
            };
//          Message scheme:  "\n i. TeamName: score"
            endMessage.append("\n ").append(rankColor).append(currentPlace).append(ChatColor.WHITE).append(". ").append(team.getDisplayName()).append(": ").append(team.getScore());
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.sendMessage(player, endMessage.toString());
        }
    }

    public void handlePlayerMove(Player player) {
        if (gameState == GameState.IDLE) {
            return;
        }
        Location playerLocation = player.getLocation();

        if (teamsManager.getTeam(player) == null) {
            return;
        }
        if (playerBlockMap.get(player) == null) {
            return;
        }

        for (String blockName : playerBlockMap.get(player)) {
            Material playerBlock = Material.getMaterial(blockName);
            if (playerBlock == playerLocation.getBlock().getType() || playerBlock == playerLocation.getBlock().getRelative(0, -1, 0).getType()) {
                playerFoundBlock(player);
            }
        }
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

        BSTeam team = teamsManager.getTeam(player);

        if (teamScoreIncrementPerPlayer) {
            teamsManager.incrementTeamScore(team);
        } else {
            // else check if teamscore already incremented
            for (Player p : team.getPlayers()) {
                if (playersWhoFoundBlock.contains(p)) {
                    teamFoundBlock = true;
                    break;
                }
            }
            if (!teamFoundBlock) {
                teamsManager.incrementTeamScore(team);
            }
        }

        playersWhoFoundBlock.add(player);

        // block found message
        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.sendMessage(p, player.getDisplayName() + " has found their block! (" + ChatColor.GOLD
                    + playerBlockMap.get(player).get(0).replace("_", " ") + ChatColor.RESET + ")");
        }
        playerBlockMap.remove(player);

        if (!isGameEnding(false)) {
            playBlockFoundSound(plugin, settings, player, true);
        }

        if (firstToWin) {
            // if not allPlayersRequiredForTeamWin set teammates block to found
            if (!allPlayersRequiredForTeamWin) {
                for (BSTeam t : teamsManager.getTeams()) {
                    for (Player p : t.getPlayers()) {
                        playersWhoFoundBlock.add(p);
                        playerBlockMap.remove(p);
                    }
                }
                endRound();
                return;
            }
            for (Player p : team.getPlayers()) {
                if (!playersWhoFoundBlock.contains(p)) {
                    return;
                }
            }
            endRound();
            return;
        }
        if (playerBlockMap.isEmpty()) {
            endRound();
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
    private void assignBlocks() {
        ArrayList<String> blockNames = null;
        String blockName;

        ArrayList<ArrayList<ArrayList<String>>> blockList = plugin.categoryTree.getBlockList(settings);

        if (blockAssignmentMode.equals(BAMode.ONE_PER_ROUND)) {
            blockNames = getRandomBlock(blockList);
        }
        for (BSTeam team : teamsManager.getTeams()) {
            if (blockAssignmentMode.equals(BAMode.ONE_PER_TEAM)) {
                blockNames = getRandomBlock(blockList);
            }
            for (Player player : team.getPlayers()) {
                if (blockAssignmentMode.equals(BAMode.ONE_PER_PLAYER)) {
                    blockNames = getRandomBlock(blockList);
                }
                playerBlockMap.put(player, blockNames);
                assert blockNames != null;
                blockName = blockNames.get(0);
                blockName = blockName.replaceAll("_", " ");
                assert player != null;
                plugin.sendMessage(player, "Your block is: " + ChatColor.GOLD + blockName);
            }
        }
    }

    // TODO this should be in category tree probably
    public ArrayList<String> getRandomBlock(ArrayList<ArrayList<ArrayList<String>>> blockList) {
        Random random = new Random();

        if (blockList.isEmpty()) {
            throw new IllegalArgumentException("Block list is empty");
        }
        ArrayList<ArrayList<String>> block = blockList.get(random.nextInt(blockList.size()));

        // Return random variant of the block
        return block.get(random.nextInt(block.size()));
    }

    private boolean isGameEnding(boolean teamsEliminated) {
        boolean endGameIfOneTeamRemaining = settings.getBoolean("endGameIfOneTeamRemaining");
        int teamsSizeAfterElimination = teamsManager.getTeams().size();

        if (!teamsEliminated) {
            teamsSizeAfterElimination -= getTeamsToEliminate().size();
        }
        if (teamsSizeAfterElimination == 0) {
            return true;
        }
        if (endGameIfOneTeamRemaining && teamsSizeAfterElimination == 1) {
            return true;
        } else
            return roundsRemaining == 0;
    }
}
