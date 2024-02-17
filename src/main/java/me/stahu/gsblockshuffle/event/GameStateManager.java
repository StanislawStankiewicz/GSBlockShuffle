package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;


/**
 * The GameStateManager class is used to manage and track the game state.
 * Tracks: teams / players and their assigned blocks, the current game state, and the game timer.
 */
public class GameStateManager {
    private final GSBlockShuffle plugin;
    private YamlConfiguration settings;
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    public Map<String, Team> teams;
    private int gameState; // 0 - not started, 1 - started
    public int roundsPerGame;
    private int roundsRemaining;
    public int secondsInRound;
    public int secondsInRoundBreak;
    private int secondsLeft;
    private int roundTickTask;
    private BossBar bossBar;
    public Map<String, ArrayList<String>> playerBlockMap;
    public HashSet<Player> playersWithFoundBlock = new HashSet<>();
    public ArrayList<Player> playersWithATeam = new ArrayList<>();
    private int roundBreakTickTask;

    public boolean setGameState(int gameState) {
        if (this.gameState == gameState) {
            return false;
        }
        if (gameState == 0) {
            this.gameState = gameState;
            clearPlayerBlocks();
            playersWithATeam.clear();
            clearTeams();

            return true;
        }
        if (gameState == 1) {
            this.gameState = gameState;
            startGame();
            return true;
        }
        return false;
    }

    public int getGameState() {
        return gameState;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    public GameStateManager(YamlConfiguration settings, GSBlockShuffle plugin) {
        this.teams = new HashMap<>();

        this.playerBlockMap = new HashMap<>();

        this.gameState = 0;

        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();

        this.settings = settings;
        this.plugin = plugin;


        this.roundsPerGame = settings.getInt("roundsPerGame");
        this.secondsInRound = settings.getInt("roundTime"); // 20 ticks per second
    }

    // Start game
    public void startGame() {
        // Adding players with no team to their own team
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!playersWithATeam.contains(player)) {
                player.sendRawMessage("You have been assigned to your own team.");
                addTeam(player.getName(), ChatColor.WHITE);
                addPlayerToTeam(player.getName(), player.getName());
            }
        }

        Objective objective = scoreboard.registerNewObjective("Score", "dummy", "Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Team team : teams.values()) {
            Score score = objective.getScore(team.getName());
            score.setScore(0);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
        System.out.println(scoreboard.getEntries());

        roundsRemaining = roundsPerGame;
        newRound();
    }

    public void newRound() {
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendRawMessage("New round has started!");
        }
        bossBar = this.createBossBar();
        secondsLeft = secondsInRound;

        assignRandomBlocks();

        roundTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundTick, 0, 20);
    }

    private void roundTick() {
        boolean muteSounds = settings.getBoolean("muteSounds");

        if (secondsLeft-- <= 0) {
            endRound();
            return;
        }
        double progress = secondsLeft / (double) (secondsInRound);
        updateBossBar(progress);
        System.out.println("Round tick: " + secondsLeft);
        System.out.println("muteSounds: " + muteSounds);
        if (secondsLeft < 61 && !muteSounds) {
            pingPlayers(secondsLeft);
        }
    }

    public void endRound() {
        Bukkit.getScheduler().cancelTask(this.roundTickTask);
        Team team;
        int membersWithoutBlock;
        HashSet<Team> eliminatedTeams = new HashSet<>();
        boolean eliminateAfterRound = settings.getBoolean("eliminateAfterRound");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendRawMessage("Round has ended!");
        }
        // this part of code checks if players have found their blocks and eliminates teams if necessary
        for (String teamName : teams.keySet()) {
            team = teams.get(teamName);
            membersWithoutBlock = 0;
            for (String playerName : team.getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                if (!playersWithFoundBlock.contains(player)) {
                    player.sendRawMessage("You did not find your block!");
                    if (eliminateAfterRound) {
                        membersWithoutBlock++;
                        playersWithATeam.remove(player);
                        eliminatedTeams.add(team);
                    }
                }
            }
            if (allPlayersRequiredForTeamWin && membersWithoutBlock > 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendRawMessage(team.getName() + " did not find all their blocks!");
                }
                eliminatedTeams.add(team);
            }
        }
        // Cleanup
        this.eliminateTeams(eliminatedTeams);
        // TODO check if unnecessary after unregistering
        this.removeEmptyTeams();
        playersWithFoundBlock.clear();
        playerBlockMap.clear();
        this.clearBossBar();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendRawMessage(--roundsRemaining + " rounds remaining!");
        }
        if (eliminateAfterRound && teams.size() == 1) {
            endGame();
            return;
        }
        if (roundsRemaining == 0) {
            endGame();
            return;
        }
        System.out.println("invoking round break");
        roundBreak();
    }

    public void roundBreak() {
        secondsInRoundBreak = settings.getInt("roundBreakTime");
        secondsLeft = secondsInRoundBreak;
        System.out.println("Round break started for " + secondsLeft + " seconds!");
        bossBar = this.createBossBar();

        roundBreakTickTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::roundBreakTick, 0, 20);
    }

    private void roundBreakTick() {
        System.out.println("Round break 2 started for " + secondsLeft + " seconds!");
        if (secondsLeft-- <= 0) {
            this.bossBar.removeAll();
            newRound();
            return;
        }
        double progress = secondsLeft / (double) (secondsInRoundBreak);
        updateBreakBossBar(progress);
    }

    private void endGame() {
        bossBar.removeAll();
        this.clearScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendRawMessage("Game over!");
        }
        setGameState(0);
    }

    public Team addTeam(String teamName, ChatColor teamColor) {
        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(teamColor);
        teams.put(teamName, team);
        System.out.println("Team " + teamName + " added!");

        return team;
    }

    public void removePlayerFromAllTeams(String playerName) {
        for (Team team : teams.values()) {
            team.removeEntry(playerName);
        }
    }

    public void addPlayerToTeam(String teamName, String playerName) {
        Team team = teams.get(teamName);
        team.addEntry(playerName);
        playersWithATeam.add(Bukkit.getPlayer(playerName));
    }

    public void clearTeams() {
        for (Team team : teams.values()) {
            team.unregister();
        }
        teams.clear();
    }

    private void assignRandomBlocks() {
        String blockAssignmentMode = settings.getString("blockAssignmentMode");
        ArrayList<String> blockName = null;
        ArrayList<String> blockAssignmentModes = new ArrayList<>();
        blockAssignmentModes.add("onePerPlayer");
        blockAssignmentModes.add("onePerTeam");
        blockAssignmentModes.add("onePerRound");

        if (!blockAssignmentModes.contains(blockAssignmentMode)) {
            // TODO raise error
            System.out.println("Error: blockAssignmentMode incorrectly set to: " + blockAssignmentMode);
            return;
        }
//        if (Objects.equals(blockAssignmentMode, "onePerPlayer")) {
//            for (Team team : teams.values()) {
//                for (String playerName : team.getEntries()) {
//                    blockName = plugin.categoryTree.getRandomBlock(settings);
//                    assignBlockToPlayer(playerName, blockName);
//                    Player player = Bukkit.getPlayer(playerName);
//                    player.sendRawMessage("Your block is: " + blockName);
//                }
//            }
//            return;
//        }
//
//        if (Objects.equals(blockAssignmentMode, "onePerTeam")) {
//            for (Team team : teams.values()) {
//                blockName = plugin.categoryTree.getRandomBlock(settings);
//                for (String playerName : team.getEntries()) {
//                    assignBlockToPlayer(playerName, blockName);
//                    Player player = Bukkit.getPlayer(playerName);
//                    player.sendRawMessage("Your teams block is: " + blockName);
//                }
//            }
//            return;
//        }
//
//        if (Objects.equals(blockAssignmentMode, "onePerRound")) {
//            blockName = plugin.categoryTree.getRandomBlock(settings);
//            for (Team team : teams.values()) {
//                for (String playerName : team.getEntries()) {
//                    assignBlockToPlayer(playerName, blockName);
//                    Player player = Bukkit.getPlayer(playerName);
//                    player.sendRawMessage("The block is: " + blockName);
//                }
//            }
//            return;
//        }
        // TODO refactor
        if (Objects.equals(blockAssignmentMode, "onePerRound")) {
            blockName = plugin.categoryTree.getRandomBlock(settings);
        }
        for (Team team : teams.values()) {
            if (Objects.equals(blockAssignmentMode, "onePerTeam")) {
                blockName = plugin.categoryTree.getRandomBlock(settings);
            }
            for (String playerName : team.getEntries()) {
                if (Objects.equals(blockAssignmentMode, "onePerPlayer")) {
                    blockName = plugin.categoryTree.getRandomBlock(settings);
                }
                assignBlockToPlayer(playerName, blockName);
                Player player = Bukkit.getPlayer(playerName);
                player.sendRawMessage("Your block is: " + blockName);
            }
        }
    }

    // TODO refactor
    public void playerFoundBlock(Player player) {
        String blockAssignmentMode = settings.getString("blockAssignmentMode");
        boolean firstToWin = settings.getBoolean("firstToWin");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");
        boolean teamScoreIncrementPerPlayer = settings.getBoolean("teamScoreIncrementPerPlayer");
        Team team = teams.get(player.getName());

        if (Objects.equals(blockAssignmentMode, "onePerPlayer")) {
            playerBlockMap.remove(player.getName());
            playersWithFoundBlock.add(player);

            // if any player for team win
            if (!allPlayersRequiredForTeamWin) {
                scoreboard.getObjective("Score").getScore(team.getName()).setScore(scoreboard.getObjective("Score").getScore(team.getName()).getScore() + 1);
                if (!teamScoreIncrementPerPlayer) {
                    for (String playerName : team.getEntries()) {
                        playersWithFoundBlock.add(Bukkit.getPlayer(playerName));
                        playerBlockMap.remove(playerName);
                    }
                }
            }
            if (teamScoreIncrementPerPlayer) {
                scoreboard.getObjective("Score").getScore(team.getName()).setScore(scoreboard.getObjective("Score").getScore(team.getName()).getScore() + 1);
            }
            if (firstToWin) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.sendRawMessage("Ending round");
                }
                endRound();
            } else if (playerBlockMap.isEmpty()) {
                endRound();
            }
        }

        if (Objects.equals(blockAssignmentMode, "onePerTeam")) {
            if (firstToWin) {
                for (String playerName : team.getEntries()) {
                    playersWithFoundBlock.add(Bukkit.getPlayer(playerName));
                }
                // TODO refactor
                // add 1 to the teams score
                scoreboard.getObjective("Score").getScore(team.getName()).setScore(scoreboard.getObjective("Score").getScore(team.getName()).getScore() + 1);

                endRound();
            } else if (playerBlockMap.isEmpty()) {
                endRound();
            }
        }

        if (Objects.equals(blockAssignmentMode, "onePerRound")) {
            // TODO
            scoreboard.getObjective("Score").getScore(team.getName()).setScore(scoreboard.getObjective("Score").getScore(team.getName()).getScore() + 1);
            if (firstToWin) {
                endRound();
            } else if (playerBlockMap.isEmpty()) {
                endRound();
            }
        }
    }

    private void removeEmptyTeams() {
        for (String teamName : teams.keySet()) {
            Team team = teams.get(teamName);
            if (team.getEntries().isEmpty()) {
                team.unregister();
                teams.remove(teamName);
            }
        }
    }

    private void eliminateTeams(HashSet<Team> eliminatedTeams) {
        for (Team eliminatedTeam : eliminatedTeams) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendRawMessage(eliminatedTeam.getName() + " has been eliminated!");
            }
            for (Team team : eliminatedTeams) {
                teams.remove(team.getName());
            }
            eliminatedTeam.unregister();
        }
    }

    private void assignBlockToPlayer(String playerName, ArrayList<String> blockNameList) {
        playerBlockMap.put(playerName, blockNameList);
    }

    public void clearPlayerBlocks() {
        playerBlockMap.clear();
    }

    public void clearScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    private BossBar createBossBar() {
        BossBar bossBar = Bukkit.createBossBar("Something might've failed.", BarColor.GREEN, BarStyle.SOLID);
        for (Player player : playersWithATeam) {
            bossBar.addPlayer(player);
        }
        return bossBar;
    }

    private void updateBossBar(double progress) {
        ChatColor timerColor;

        // TODO do this like a binary search
        if (progress < 0.1) {
            timerColor = ChatColor.DARK_RED;
        } else if (progress < 0.2) {
            bossBar.setColor(BarColor.RED);
            timerColor = ChatColor.RED;
        } else if (progress < 0.3) {
            timerColor = ChatColor.GOLD;
        } else if (progress < 0.5) {
            bossBar.setColor(BarColor.YELLOW);
            timerColor = ChatColor.YELLOW;
        } else if (progress < 0.75) {
            bossBar.setColor(BarColor.GREEN);
            timerColor = ChatColor.GREEN;
        } else {
            timerColor = ChatColor.DARK_GREEN;
            bossBar.setColor(BarColor.GREEN);
        }
        this.bossBar.setProgress(progress);
        this.bossBar.setTitle(ChatColor.WHITE + "Time left: " + timerColor + secondsLeft + ChatColor.WHITE + "s");
    }
    private void updateBreakBossBar(double progress) {
        System.out.println("Progress: " + progress);
        this.bossBar.setProgress(progress);
        this.bossBar.setColor(BarColor.BLUE);
        this.bossBar.setTitle(ChatColor.WHITE + "New block in: " + ChatColor.DARK_AQUA + secondsLeft + ChatColor.WHITE + "s");
    }

    public void clearBossBar() {
        bossBar.removeAll();
    }

    private void pingPlayers(int secondsLeft) {
        System.out.println("Got to pingPlayers");
        if (secondsLeft == 60 && secondsInRound > 120) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendRawMessage("1 minute remaining!");
                pingPlayerNTimes(player, 1, 4);
            }
        } else if (secondsLeft == 30 && secondsInRound > 60) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendRawMessage("30 seconds remaining!");
                pingPlayerNTimes(player, 2, 4);
            }
        } else if (secondsLeft == 10 && secondsInRound > 30) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendRawMessage("10 seconds remaining!");
                pingPlayerNTimes(player, 3, 4);
            }
        } else if (secondsLeft < 10 && secondsInRound > 30) {
            System.out.println("Got to playSound");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5F, 1.2F);
            }
        }
    }

    // TODO make delay a constant if not needed
    private void pingPlayerNTimes(Player player, int n, long delay) {
        for (int i = 0; i < n; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playPingSound(player), i * delay);
        }
    }

    private void playPingSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.5F);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1F);
    }
}

