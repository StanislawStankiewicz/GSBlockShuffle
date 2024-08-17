package me.stahu.gsblockshuffle.event.handler;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.event.BlockAssignEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class BlockAssignHandler {

    final MessageController messageController;

    public void sendAssignmentMessage(BlockAssignEvent event) {
        String blockName = event.getBlock().names().get(0)
                .replace("_", " ");
        TextComponent message = new TextComponent("Your block is: " + ChatColor.GOLD + blockName);

        messageController.sendMessage(event.getPlayer(), message);
    }
}
