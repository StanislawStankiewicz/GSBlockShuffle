package me.stahu.gsblockshuffle.view.cli.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Command {
    protected List<String> filterCompletions(List<String> completions, String arg) {
        return completions.stream().filter(completion -> completion.toLowerCase().startsWith(arg)).toList();
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
