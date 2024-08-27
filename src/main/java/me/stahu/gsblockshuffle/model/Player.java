package me.stahu.gsblockshuffle.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.view.sound.Note;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Optional;

@Getter @Setter
@RequiredArgsConstructor
public class Player {
    final PlayerAPI api;
    Optional<Team> team = Optional.empty();
    Optional<Block> assignedBlock = Optional.empty();
    boolean isFoundBlock;

    public String getName() {
        return api.getDisplayName();
    }

    public String getDisplayName() {
        return api.getDisplayName();
    }

    public void playSound(Note note) {
        api.playSound(note);
    }

    public void sendMessage(String message) {
        api.sendMessage(message);
    }

    public void sendMessage(TextComponent message) {
        api.sendMessage(message);
    }

    public boolean hasPermission(String permission) {
        return api.hasPermission(permission);
    }

    public boolean isLeader() {
        return team.map(value -> value.getLeader().equals(this)).orElse(false);
    }
}
