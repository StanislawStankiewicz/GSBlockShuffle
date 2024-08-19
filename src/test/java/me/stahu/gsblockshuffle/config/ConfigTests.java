package me.stahu.gsblockshuffle.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTests {

    @Test
    void testLoadSettings() {
        File settingsFile = new File("src/test/resources/testSettings.yml");
        YamlConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
        Config config = new Config();

        config.loadSettings(settings);

        assertEquals(0, config.getStartDifficulty());
        assertEquals(10, config.getDifficultyCap());
        assertTrue(config.isIncreaseDifficulty());
        assertEquals(1, config.getIncreaseEveryNRounds());
        assertArrayEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toArray(), config.getCustomIncrease().toArray());
        assertFalse(config.isIncludeLowerDifficulties());
        assertTrue(config.isIncludeVariants());
        assertFalse(config.isTreatAllAsIndividualBlocks());

        assertEquals(1, config.getTotalRounds());
        assertEquals(2, config.getGameStartDelaySeconds());
        assertEquals(5, config.getRoundDurationSeconds());
        assertEquals(10, config.getBreakDurationSeconds());
        assertEquals(BlockAssignmentMode.ONE_PER_PLAYER, config.getBlockAssignmentMode());

        assertTrue(config.isAllPlayersRequiredForTeamWin());
        assertFalse(config.isTeamScoreIncrementPerPlayer());
        assertFalse(config.isEliminateAfterRound());
        assertTrue(config.isEndGameIfOneTeamRemaining());
        assertTrue(config.isFirstToWin());

        assertEquals(TeleportMode.AMOUNT_PER_TEAM, config.getTeleportMode());
        assertEquals(1, config.getTeleportsPerRound());

        assertTrue(config.isDisplaySplashTitle());
        assertFalse(config.isShowTeamCompass());
        assertFalse(config.isMuteSound());
        assertTrue(config.isDisablePvP());
    }
}
