package me.stahu.gsblockshuffle.view.cli.command;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.GameController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BlockShuffleCommands implements CommandExecutor {

    private final GameController gameController;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("bs")) {
            gameController.startGame();
        }
        return true;
    }
}
