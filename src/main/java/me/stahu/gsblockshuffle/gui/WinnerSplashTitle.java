package me.stahu.gsblockshuffle.gui;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WinnerSplashTitle {
    public static void showWinnerSplashTitle(GSBlockShuffle plugin, YamlConfiguration settings, Player player){
        player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRocin", 10, 70, 20);

        if (!settings.getBoolean("muteSounds")) {
            playWinnerSound(plugin, player);
        }
    }

    private static void playWinnerSound(GSBlockShuffle plugin, Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.529732f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.667420f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.793701f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.943874f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 1.189207f);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.529732f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.707107f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.890899f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 1.059463f);
        }, 10);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.529732f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.707107f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 0.890899f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1, 1.059463f);
        }, 13);

    }
}
