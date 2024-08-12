package me.stahu.gsblockshuffle.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class Player {
    final org.bukkit.entity.Player player;
    Team team;
    Block block;
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

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return player.getScoreboard();
    }
}
