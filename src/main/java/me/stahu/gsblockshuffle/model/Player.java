package me.stahu.gsblockshuffle.model;

import lombok.*;
import me.stahu.gsblockshuffle.view.sound.Note;

@Getter @Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Player {
    final org.bukkit.entity.Player player;
    Team team;
    Block assignedBlock;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    boolean hasFoundBlock;

    public boolean hasFoundBlock() {
        return hasFoundBlock;
    }

    public void setFoundBlock(boolean hasFoundBlock) {
        this.hasFoundBlock = hasFoundBlock;
    }

    public String getName() {
        return player.getDisplayName();
    }

    public String getDisplayName() {
        return player.getDisplayName();
    }

    public void playSound(Note note) {
        player.playSound(player.getLocation(), note.instrument(), note.volume(), note.pitch());
    }
}
