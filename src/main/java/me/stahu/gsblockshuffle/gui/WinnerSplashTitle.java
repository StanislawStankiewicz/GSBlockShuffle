package me.stahu.gsblockshuffle.gui;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import static me.stahu.gsblockshuffle.sound.Sounds.playWinnerSound;

public class WinnerSplashTitle {
    public static void showWinnerSplashTitle(GSBlockShuffle plugin, YamlConfiguration settings, Player player) {
        if (settings.getBoolean("displaySplashWinnerTitle")) {
            player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRocin", 10, 70, 20);
        }

        if (!settings.getBoolean("muteSounds")) {
            playWinnerSound(plugin, settings, player);
        }
    }

}
