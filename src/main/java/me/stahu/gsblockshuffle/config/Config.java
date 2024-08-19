package me.stahu.gsblockshuffle.config;

import lombok.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Config {
    @Setter int startDifficulty;
    int difficultyCap;
    boolean increaseDifficulty;
    int increaseEveryNRounds;
    List<Integer> customIncrease;
    boolean includeLowerDifficulties;
    boolean includeVariants;
    boolean treatAllAsIndividualBlocks;

    int totalRounds;
    int gameStartDelaySeconds;
    int roundDurationSeconds;
    int breakDurationSeconds;
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
        startDifficulty = settings.getInt("startDifficulty");
        difficultyCap = settings.getInt("difficultyCap");
        increaseDifficulty = settings.getBoolean("increaseDifficulty");
        increaseEveryNRounds = settings.getInt("increaseEveryNRounds");
        customIncrease = settings.getIntegerList("customIncrease");
        includeLowerDifficulties = settings.getBoolean("includeLowerDifficulties");
        includeVariants = settings.getBoolean("includeVariants");
        treatAllAsIndividualBlocks = settings.getBoolean("treatAllAsIndividualBlocks");

        totalRounds = settings.getInt("totalRounds");
        gameStartDelaySeconds = settings.getInt("gameStartDelaySeconds");
        roundDurationSeconds = settings.getInt("roundDurationSeconds");
        breakDurationSeconds = settings.getInt("breakDurationSeconds");
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
