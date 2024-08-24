package me.stahu.gsblockshuffle.api;

import me.stahu.gsblockshuffle.view.sound.Note;
import net.md_5.bungee.api.chat.TextComponent;

public interface PlayerAPI {
    String getName();

    String getDisplayName();

    void playSound(Note note);

    void sendMessage(String message);

    void sendMessage(TextComponent message);

    String getBlockNameBelow(int offset);
}
