package me.stahu.gsblockshuffle.controller;

import me.stahu.gsblockshuffle.model.Player;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageController {

    public void sendMessage(Player player, TextComponent message) {
        player.getPlayer().sendMessage(message.getText());
    }
}
