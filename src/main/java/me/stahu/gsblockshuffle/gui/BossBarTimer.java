package me.stahu.gsblockshuffle.gui;

import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarTimer {
    private TeamsManager teamsManager;
    private BossBar bossBar;


    public BossBarTimer(TeamsManager teamsManager) {
        this.teamsManager = teamsManager;
    }
    /**
     * Creates a new boss bar with a default message, color, and style.
     * The boss bar is initially green and solid, with the message "Something might've failed.".
     * The boss bar is then added to all players who are part of a team.
     *
     * @return The newly created boss bar.
     */
    public void createBossBar() {
        BossBar bossBar = Bukkit.createBossBar("Something might've failed.", BarColor.GREEN, BarStyle.SOLID);
        for (Player player : teamsManager.getPlayersWithATeam()) {
            bossBar.addPlayer(player);
        }
        this.bossBar = bossBar;
    }
    /**
     * Updates the boss bar's progress, color, and title based on the remaining time in the round.
     * The progress of the boss bar is set to the provided progress value.
     * The color of the boss bar changes from green to red as the time decreases.
     * The title of the boss bar displays the remaining time in seconds.
     *
     * @param progress The progress of the boss bar, represented as a double value between 0 and 1.
     */
    public void updateBossBar(double progress, int secondsLeft) {
        ChatColor timerColor;

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
        this.bossBar.setTitle(ChatColor.WHITE + "Time left: "+ timerColor + String.format("%02d", secondsLeft / 60) + ChatColor.WHITE + ":" + timerColor + String.format("%02d", secondsLeft % 60));
    }

    /**
     * Updates the boss bar during the break between rounds.
     * The progress of the boss bar is set to the provided progress value.
     * The color of the boss bar is set to blue.
     * The title of the boss bar is set to display the time left until the new block is assigned.
     *
     * @param progress The progress of the boss bar, represented as a double value between 0 and 1.
     */
    public void updateBreakBossBar(double progress, int secondsLeft) {
        this.bossBar.setProgress(progress);
        this.bossBar.setColor(BarColor.BLUE);
        this.bossBar.setTitle(ChatColor.WHITE + "New block in: " + ChatColor.DARK_AQUA + String.format("%02d", secondsLeft / 60) + ChatColor.WHITE + ":" + ChatColor.DARK_AQUA + String.format("%02d", secondsLeft % 60));
    }

    public void clearBossBars() {
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }
}
