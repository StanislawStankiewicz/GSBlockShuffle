package me.stahu.gsblockshuffle.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import me.stahu.gsblockshuffle.view.sound.Note;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@AllArgsConstructor
@EqualsAndHashCode
public class BukkitPlayerAPI implements PlayerAPI {
    Player bukkitPlayer;

    @Override
    public String getName() {
        return bukkitPlayer.getName();
    }

    @Override
    public String getDisplayName() {
        return bukkitPlayer.getDisplayName();
    }

    @Override
    public void playSound(Note note) {
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), note.instrument(), note.volume(), note.pitch());
    }

    @Override
    public void sendMessage(String message) {
        bukkitPlayer.sendMessage(message);
    }

    @Override
    public void sendMessage(TextComponent message) {
        bukkitPlayer.spigot().sendMessage(message);
    }

    @Override
    public String getBlockAtPlayerLocation(int offset) {
        return bukkitPlayer.getLocation().add(0, offset, 0).getBlock().getType().name();
    }

    @Override
    public boolean hasPermission(String permission) {
        return bukkitPlayer.hasPermission(permission);
    }
}
