package me.stahu.gsblockshuffle.controller;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.model.Player;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.Set;

@RequiredArgsConstructor
public class MessageController {

    final Set<Player> players;
    final TextComponent prefix = new TextComponent("[" + ChatColor.BLUE + "GSBlockShuffle" + ChatColor.RESET + "] ");

    public void sendMessage(Player player, TextComponent message) {
        player.sendMessage(addPrefix(prefix, message));
    }

    public void sendMessageToAll(TextComponent message) {
        for (Player player : players) {
            player.sendMessage(addPrefix(prefix, message));
        }
    }

    public void commandResponse(Player player, TextComponent message, boolean success) {
        ChatColor color = success ? ChatColor.GREEN : ChatColor.RED;
        message.setColor(color.asBungee());
        player.sendMessage(message);
    }

    private TextComponent addPrefix(TextComponent prefix, TextComponent message) {
        TextComponent finalMessage = prefix.duplicate();
        finalMessage.addExtra(message);
        return finalMessage;
    }
}
