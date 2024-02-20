package me.stahu.gsblockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBase {
    protected List<String> filterCompletions(List<String> completions, String arg) {
        return completions.stream().filter(completion -> completion.startsWith(arg)).toList();
    }
    /**
     * Retrieves all online player names for tab completion.
     *
     * @return A list of player names.
     */
    protected List<String> playerList() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
