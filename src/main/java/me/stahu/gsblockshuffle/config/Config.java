package me.stahu.gsblockshuffle.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@NoArgsConstructor(force = true)
public class Config {
    int difficulty;
    int difficultyCap;
    boolean increaseDifficulty;
    int increaseEveryNRounds;
    int[] customIncrease;
    boolean includeLowerDifficulties;
    boolean includeVariants;
    boolean treatAllAsIndividualBlocks;

    int totalRounds;
    int roundTimeSeconds;
    int breakTimeSeconds;
    BlockAssignmentMode blockAssignmentMode;

    boolean isAllPlayersRequiredForTeamWin;
    boolean isTeamScoreIncrementPerPlayer;
    boolean isEliminateAfterRound;
    boolean isEndGameIfOneTeamRemaining;
    boolean isFirstToWin;

    TeleportMode teleportMode;
    int teleportsPerRound;

    boolean displaySplashTitle;
    boolean showTeamCompass;
    boolean muteSound;
    boolean disablePvP;

    final File settingsFile;
    final YamlConfiguration settings;

    public Config(File settingsFile) {
        this.settingsFile = settingsFile;
        this.settings = YamlConfiguration.loadConfiguration(settingsFile);
        loadSettings(settings);
    }

    void loadSettings(YamlConfiguration settings) {
        difficulty = settings.getInt("difficulty");
        difficultyCap = settings.getInt("difficultyCap");
        increaseDifficulty = settings.getBoolean("increaseDifficulty");
        increaseEveryNRounds = settings.getInt("increaseEveryNRounds");
        customIncrease = settings.getIntegerList("customIncrease").stream().mapToInt(i -> i).toArray();
        includeLowerDifficulties = settings.getBoolean("includeLowerDifficulties");
        includeVariants = settings.getBoolean("includeVariants");
        treatAllAsIndividualBlocks = settings.getBoolean("treatAllAsIndividualBlocks");

        totalRounds = settings.getInt("totalRounds");
        roundTimeSeconds = settings.getInt("roundTimeSeconds");
        breakTimeSeconds = settings.getInt("breakTimeSeconds");
        blockAssignmentMode = BlockAssignmentMode.fromString(settings.getString("blockAssignmentMode"));

        isAllPlayersRequiredForTeamWin = settings.getBoolean("allPlayersRequiredForTeamWin");
        isTeamScoreIncrementPerPlayer = settings.getBoolean("teamScoreIncrementPerPlayer");
        isEliminateAfterRound = settings.getBoolean("eliminateAfterRound");
        isEndGameIfOneTeamRemaining = settings.getBoolean("endGameIfOneTeamRemaining");
        isFirstToWin = settings.getBoolean("firstToWin");

        teleportMode = TeleportMode.fromString(settings.getString("teleportMode"));
        teleportsPerRound = settings.getInt("teleportsPerRound");

        displaySplashTitle = settings.getBoolean("displaySplashTitle");
        showTeamCompass = settings.getBoolean("showTeamCompass");
        muteSound = settings.getBoolean("muteSound");
        disablePvP = settings.getBoolean("disablePvP");
    }
}
