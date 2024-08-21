package me.stahu.gsblockshuffle.model;

import lombok.*;
import me.stahu.gsblockshuffle.view.sound.Note;

@Getter @Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Player {
    final org.bukkit.entity.Player serverPlayer;
    Team team;
    Block assignedBlock;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    boolean hasFoundBlock;
    boolean isOnline = true;

    public boolean hasFoundBlock() {
        return hasFoundBlock;
    }

    public void setFoundBlock(boolean hasFoundBlock) {
        this.hasFoundBlock = hasFoundBlock;
    }

    public String getName() {
        return serverPlayer.getDisplayName();
    }

    public String getDisplayName() {
        return serverPlayer.getDisplayName();
    }

    public void playSound(Note note) {
        serverPlayer.playSound(serverPlayer.getLocation(), note.instrument(), note.volume(), note.pitch());
    }
}
