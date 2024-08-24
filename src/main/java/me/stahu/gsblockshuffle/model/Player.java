package me.stahu.gsblockshuffle.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.stahu.gsblockshuffle.api.PlayerAPI;
import me.stahu.gsblockshuffle.view.sound.Note;

@Getter @Setter
@RequiredArgsConstructor
public class Player {
    final PlayerAPI api;
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
        return api.getDisplayName();
    }

    public String getDisplayName() {
        return api.getDisplayName();
    }

    public void playSound(Note note) {
        api.playSound(note);
    }
}
