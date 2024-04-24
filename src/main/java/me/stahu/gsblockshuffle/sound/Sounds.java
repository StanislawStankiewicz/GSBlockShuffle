package me.stahu.gsblockshuffle.sound;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.team.BSTeam;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Sounds {
    public static void playWinnerSound(GSBlockShuffle plugin, YamlConfiguration settings, Player player) {
        if (settings.getBoolean("muteSounds")) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.529732f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.667420f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.793701f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.943874f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 1.189207f);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playWinChord(player), 10);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playWinChord(player), 13);
    }

    private static void playWinChord(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.529732f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.707107f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.890899f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 1.059463f);
    }

    public static void playEliminationSound(GSBlockShuffle plugin, YamlConfiguration settings, Player player) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1.059463f);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.840896f), 4);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.707107f), 8);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.529732f), 12);
    }

    public static void playBlockFoundSound(GSBlockShuffle plugin, YamlConfiguration settings, Player player, boolean blockFound) {
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

    public static void pingPlayers(GSBlockShuffle plugin, YamlConfiguration settings, int secondsLeft) {
        int secondsInRound = settings.getInt("roundTime");

        if (settings.getBoolean("muteSounds")) {
            return;
        }
        if (secondsLeft == 60 && secondsInRound > 120) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(plugin, settings, player, 1, 4);
            }
        } else if (secondsLeft == 30 && secondsInRound > 60) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(plugin, settings, player, 2, 4);
            }
        } else if (secondsLeft == 10 && secondsInRound > 30) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                pingPlayerNTimes(plugin, settings, player, 3, 4);
            }
        } else if (secondsLeft < 10 && secondsInRound > 30) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5F, 1.2F);
            }
        }
    }

    public static void pingPlayerNTimes(GSBlockShuffle plugin, YamlConfiguration settings, Player player, int n, long delay) {
        if (settings.getBoolean("muteSounds")) {
            return;
        }

        for (int i = 0; i < n; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playPingSound(settings, player), i * delay);
        }
    }

    private static void playPingSound(YamlConfiguration settings, Player player) {
        if (settings.getBoolean("muteSounds")) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.5F);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1F);
    }

    public static void playRoundCountdownSound(GSBlockShuffle plugin, YamlConfiguration settings, TeamsManager teamsManager) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }
        playSoundToAllPlayers(settings, teamsManager, 0.629961F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playSoundToAllPlayers(settings, teamsManager, 0.629961F), 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playSoundToAllPlayers(settings, teamsManager, 1.259921F), 40);
    }

    // TODO rename
    private static void playSoundToAllPlayers(YamlConfiguration settings, TeamsManager teamsManager, float pitch) {
        boolean muteSounds = settings.getBoolean("muteSounds");
        if (muteSounds) {
            return;
        }
        for (BSTeam team : teamsManager.getTeams()) {
            for (Player player : team.getPlayers()) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, pitch);
            }
        }
    }

}
