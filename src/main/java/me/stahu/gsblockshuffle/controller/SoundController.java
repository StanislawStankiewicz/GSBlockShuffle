package me.stahu.gsblockshuffle.controller;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.view.sound.Note;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class SoundController {

    final GSBlockShuffle plugin;
    final BukkitScheduler scheduler;

    final Set<Player> players;

    public void playSound(Player player, Note note) {
        player.playSound(note);
    }

    public void playSound(Player player, Note note, int delayTicks) {
        scheduler.scheduleSyncDelayedTask(plugin, () -> playSound(player, note), delayTicks);
    }

    public void playSoundToAll(Note note) {
        players.forEach(player -> player.playSound(note));
    }

    public void playSoundToAll(Note note, int delayTicks) {
        scheduler.scheduleSyncDelayedTask(plugin, () -> playSoundToAll(note), delayTicks);
    }

    public void playChord(Player player, List<Note> chord) {
        chord.forEach(player::playSound);
    }

    public void playChord(Player player, List<Note> chord, int delayTicks) {
        scheduler.scheduleSyncDelayedTask(plugin, () -> playChord(player, chord), delayTicks);
    }

    public void playChordToAll(List<Note> chord) {
        players.forEach(player -> playChord(player, chord));
    }

    public void playChordToAll(List<Note> chord, int delayTicks) {
        scheduler.scheduleSyncDelayedTask(plugin, () -> playChordToAll(chord), delayTicks);
    }
}
