package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.BossBarTimer;
import me.stahu.gsblockshuffle.gui.WinnerSplashTitle;
import me.stahu.gsblockshuffle.team.TeamsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;


public class GameStateManager {
    private final GSBlockShuffle plugin;
    private final YamlConfiguration settings;
    private final TeamsManager teamsManager;
    private int gameState; // 0 - not started, 1 - started
    private int roundsRemaining;
    public int secondsInRoundBreak;
    private int secondsLeft;
    private int currentRound = 0;
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

        playRoundCountdownSound();

        roundStartTask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::newRound, 40);
    }

    public void newRound() {
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);

        //create compass based on setings.get("showTeamCompass")
        if (settings.getBoolean("showTeamCompass")) {
            plugin.teammateCompass.createCompassBars();
        } else {
            plugin.teammateCompass.clearCompassBars();
        }

        assignRandomBlocks();

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
            pingPlayers(secondsLeft);
        }
    }

    public void endRound() {
        Bukkit.getScheduler().cancelTask(this.roundTickTask);
        int membersWithoutBlock;
        HashSet<Team> eliminatedTeams = new HashSet<>();
        boolean eliminateAfterRound = settings.getBoolean("eliminateAfterRound");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");

        // this part of code checks if players have found their blocks and eliminates teams if necessary
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
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendRawMessage(team.getDisplayName() + " eliminated!");
                }
                eliminatedTeams.add(team);
            }
        }
        // Play loss sound to players who didn't find their block
        for (Player player : teamsManager.getPlayersWithATeam()) {
            if (!playersWithFoundBlock.contains(player)) {
                playBlockFoundSound(player, false);
            }
        }

        // Cleanup
        playersWithFoundBlock.clear();
        playerBlockMap.clear();
        bossBarTimer.clearBossBars();
        this.eliminateTeams(eliminatedTeams);
        teamsManager.playerTpUsed.clear();
        teamsManager.teamTpUsed.clear();

        roundsRemaining--;

        // endGame() conditions
        if (eliminateAfterRound && teamsManager.teams.size() == 1) {
            endGame();
            return;
        } else if (roundsRemaining == 0) {
            endGame();
            return;
        }

        currentRound++;

        // handle increasing difficulty
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

        roundBreak();
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

        if(blockList.isEmpty()){
            settings.set("difficulty", previousDifficulty);
        }
    }

    /**
     * Initiates the break between rounds.
     * This method sets the duration of the round break based on the "roundBreakTime" setting.
     * It then creates a new boss bar and schedules the roundBreakTick method to be called every tick (20 ticks = 1 second).
     */
    public void roundBreak() {
        secondsInRoundBreak = settings.getInt("roundBreakTime");
        secondsLeft = secondsInRoundBreak;
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
            playRoundCountdownSound();
        }

        double progress = secondsLeft / (double) (secondsInRoundBreak);
        bossBarTimer.updateBreakBossBar(progress, secondsLeft);
    }

    /**
     * Sends an end game message to all players.
     * This method constructs a message indicating the end of the game and the final scores of each team.
     * The message is then sent to all online players.
     * Note: The getTeamScore method is used to retrieve the score of each team.
     */
    public void endGame() {
        Bukkit.getScheduler().cancelTask(this.roundStartTask);
        Bukkit.getScheduler().cancelTask(this.roundBreakTickTask);
        Bukkit.getScheduler().cancelTask(this.roundTickTask);

        List<Map.Entry<Team, Integer>> teamPlaceList = teamsManager.getTeamPlaceList();

        // send winner splash title to all 1st places
        for (Map.Entry<Team, Integer> entry : teamPlaceList) {
            if (entry.getValue() != 1) {
                break;
            }
            for (String playerName : entry.getKey().getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                WinnerSplashTitle.showWinnerSplashTitle(plugin, settings, player);
            }
        }
        sendEndGameMessageToAllPlayers(teamPlaceList);

        //remove compass
        plugin.teammateCompass.clearCompassBars();
        bossBarTimer.clearBossBars();
        teamsManager.clearScoreboards();

        this.gameState = 0;
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
            endGame();
            return;
        }

        ArrayList<ArrayList<ArrayList<String>>> blockList = plugin.categoryTree.getBlockList(settings);

        System.out.println("Difficulty: " + settings.getInt("difficulty"));
        System.out.println("Block list: " + blockList);

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
// TODO refactor
    public void playerFoundBlock(Player player) {
        boolean firstToWin = settings.getBoolean("firstToWin");
        boolean allPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");
        boolean teamScoreIncrementPerPlayer = settings.getBoolean("teamScoreIncrementPerPlayer");
        boolean teamFoundBlock = false;

        Team team = teamsManager.getTeam(player);
        playBlockFoundSound(player, true);

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
        for (Team t : teamsManager.teams) {
            for (String playerName : t.getEntries()) {
                Player p = Bukkit.getPlayer(playerName);
                plugin.sendMessage(p, player.getDisplayName() + " has found their block! (" + ChatColor.GOLD
                        + playerBlockMap.get(player.getName()).get(0).replace("_", " ") + ChatColor.RESET + ")");
            }
        }

        playerBlockMap.remove(player.getName());

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
            return;
        }
        // if allPlayersRequiredForTeamWin - check if all players have found block, if so endRound
        if (allPlayersRequiredForTeamWin) {
            for (String playerName : team.getEntries()) {
                if (!playersWithFoundBlock.contains(Bukkit.getPlayer(playerName))) {
                    return;
                }
            }
            for (String playerName : team.getEntries()) {
                playersWithFoundBlock.remove(Bukkit.getPlayer(playerName));
                playerBlockMap.remove(playerName);
            }
        }
    }

    /**
     * Eliminates the specified teams from the game.
     * This method sends a message to all online players notifying them of each eliminated team.
     * It also removes the eliminated teams from the list of active teams and unregisters them from the scoreboard.
     *
     * @param eliminatedTeams A HashSet of teams to be eliminated.
     */
    private void eliminateTeams(HashSet<Team> eliminatedTeams) {
        for (Team eliminatedTeam : eliminatedTeams) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.sendMessage(player, eliminatedTeam.getDisplayName() + " has been eliminated!");
            }
            for (Team team : eliminatedTeams) {
                teamsManager.teams.remove(team);
            }
            eliminatedTeam.unregister();
        }
    }


    /**
     * Sends a ping sound to all online players based on the remaining seconds in the round.
     * The ping sound is played at 60, 30, and 10 seconds remaining, given that rounds are longer than 120, 60, and 30 seconds, respectively.
     * Additionaly a clock ticking sound is played when there are less than 10 seconds remaining.
     * If the muteSounds setting is enabled, no sound will be played.
     *
     * @param secondsLeft The number of seconds remaining in the round.
     */
    private void pingPlayers(int secondsLeft) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        int secondsInRound = settings.getInt("roundTime");

        if (muteSounds) {
            return;
        }
        if (secondsLeft == 60 && secondsInRound > 120) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(player, 1, 4);
            }
        } else if (secondsLeft == 30 && secondsInRound > 60) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(player, 2, 4);
            }
        } else if (secondsLeft == 10 && secondsInRound > 30) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(player, 3, 4);
            }
        } else if (secondsLeft < 10 && secondsInRound > 30) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5F, 1.2F);
            }
        }
    }

    /**
     * Plays a ping sound to a specific player a given number of times with a delay between each ping.
     * The ping sound is played 'n' times, where 'n' is provided as an argument.
     * The delay between each ping is also provided as an argument.
     * If the muteSounds setting is enabled, no sound will be played.
     *
     * @param player The player to whom the sound will be played.
     * @param n      The number of times the ping sound will be played.
     * @param delay  The delay (in ticks) between each ping sound.
     */
    private void pingPlayerNTimes(Player player, int n, long delay) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }

        for (int i = 0; i < n; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playPingSound(player), i * delay);
        }
    }

    /**
     * Plays a ping sound to a specific player in the game.
     * The ping sound consists of two notes, an octave apart from each other.
     * If the muteSounds setting is enabled, no sound will be played.
     *
     * @param player The player to whom the sound will be played.
     */
    private void playPingSound(Player player) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.5F);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1F);
    }

    /**
     * Plays a sound to indicate that a player has found a block.
     * The sound played depends on whether the block was found or not.
     * If the muteSounds setting is enabled, no sound will be played.
     *
     * @param player     The player to whom the sound will be played.
     * @param blockFound A boolean indicating whether the block was found or not.
     */
    private void playBlockFoundSound(Player player, boolean blockFound) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }

        if (blockFound) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.189207F);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.781797F), 3);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.781797F);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.059463F), 3);
        }
    }

    /**
     * Plays a countdown sound to all players in the game.
     * The countdown sound is played in three steps with a delay between each step.
     * If the muteSounds setting is enabled, no sound will be played.
     */
    private void playRoundCountdownSound() {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }
        playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.629961F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.629961F), 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playSoundToAllPlayers(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1.259921F), 40);
    }

    /**
     * Plays a specific sound to all players in the game, given the sound, volume, and pitch.
     * If the muteSounds setting is enabled, no sound will be played.
     *
     * @param sound  The sound to be played.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    private void playSoundToAllPlayers(Sound sound, int volume, float pitch) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }

        for (Player player : teamsManager.getPlayersWithATeam()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
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
}

