package me.stahu.gsblockshuffle.gui;

import me.stahu.gsblockshuffle.team.BSTeam;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SplashTitle {
    public static void showWinnerTitle(YamlConfiguration settings, Player player) {
        if (!settings.getBoolean("displaySplashTitle")) {
            return;
        }
        player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.AQUA + "GSBlockShuffle by stahu & MRcoin",
                10, 70, 20);
    }

    public static void showPlayerWonTitle(YamlConfiguration settings, Player player, List<BSTeam> winnerTeams) {
        if (!settings.getBoolean("displaySplashTitle")) {
            return;
        }

        StringBuilder winners = new StringBuilder();
        for (BSTeam winnerTeam : winnerTeams) {
            String winnerName = winnerTeam.getName();
            winners.append(winnerName).append(", ");
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
