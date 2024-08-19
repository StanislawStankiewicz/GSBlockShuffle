package me.stahu.gsblockshuffle.view.sound;

import org.bukkit.Sound;

public record Note(Sound instrument, int volume, float pitch) {
    public Note(Sound instrument, float pitch) {
        this(instrument, 1, pitch);
    }
}
