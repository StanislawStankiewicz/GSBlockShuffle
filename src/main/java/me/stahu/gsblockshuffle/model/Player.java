package me.stahu.gsblockshuffle.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.view.sound.Note;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;


@Getter @Setter
@RequiredArgsConstructor
public class Player {
    final PlayerAPI playerAPI;
    Team team;
    Block assignedBlock;
    boolean isFoundBlock;

    public String getName() {
        return playerAPI.getDisplayName();
    }

    public String getDisplayName() {
        return playerAPI.getDisplayName();
    }

    public void playSound(Note note) {
        playerAPI.playSound(note);
    }

    public void sendMessage(String message) {
        playerAPI.sendMessage(message);
    }

    public void sendMessage(TextComponent message) {
        playerAPI.sendMessage(message);
    }

    public boolean hasPermission(String permission) {
        return playerAPI.hasPermission(permission);
    }

    public boolean isLeader() {
        if (team == null) {
            return false;
        }
        return team.getLeader().equals(this);
    }

    public List<String> getBlockNamesUnderneath() {
        // 0 is the block at the leg level, not under them so we check both
        // the block that the player is in (like grass) and the block the player is standing on
        return List.of(
                playerAPI.getBlockAtPlayerLocation(0),
                playerAPI.getBlockAtPlayerLocation(-1));
    }
}
