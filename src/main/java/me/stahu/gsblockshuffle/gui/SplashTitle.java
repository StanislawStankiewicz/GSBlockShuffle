package me.stahu.gsblockshuffle.gui;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SplashTitle {
    public static void showYouWonTitle(YamlConfiguration settings, Player player) {
        if (!settings.getBoolean("displaySplashTitle")) {
            return;
        }
        player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRcoin",
                10, 70, 20);
    }

    public static void showPlayerWonTitle(YamlConfiguration settings, Player player, List<String> winnerNames) {
        if (!settings.getBoolean("displaySplashTitle")) {
            return;
        }

        StringBuilder winners = new StringBuilder();
        for (String winner : winnerNames) {
            winners.append(winner).append(", ");
        }
        winners.deleteCharAt(winners.length() - 2);

        player.sendTitle(ChatColor.RED + winners.toString() + "won!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRcoin",
                10, 70, 20);
    }

    public static void showEliminatedTitle(YamlConfiguration settings, Player player) {
        if (!settings.getBoolean("displaySplashTitle")) {
            return;
        }
        player.sendTitle(ChatColor.RED + "Eliminated!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRcoin",
                10, 70, 20);
    }
}
