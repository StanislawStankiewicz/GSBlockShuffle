package me.stahu.gsblockshuffle.view.sound;

import lombok.AllArgsConstructor;
import me.stahu.gsblockshuffle.controller.SoundController;
import me.stahu.gsblockshuffle.model.Player;
import org.bukkit.Sound;

import java.util.List;

@AllArgsConstructor
public class SoundPlayer {
    final SoundController soundController;
    boolean enabled;

    public void playRoundCountDownSound(int delayTicks) {
        if (!enabled) {
            return;
        }
        Note noteWait = new Note(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.629961F);
        Note noteStart = new Note(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.259921F);
        soundController.playSoundToAll(noteWait, delayTicks);
        soundController.playSoundToAll(noteWait, 20 + delayTicks);
        soundController.playSoundToAll(noteStart, 40 + delayTicks);

    }

    public void playBlockFoundSound(Player player) {
        if (!enabled) {
            return;
        }
        Note note1 = new Note(Sound.BLOCK_NOTE_BLOCK_BIT, 1.189207F);
        Note note2 = new Note(Sound.BLOCK_NOTE_BLOCK_BIT, 1.781797F);
        soundController.playSound(player, note1);
        soundController.playSound(player, note2, 3);
    }

    public void playRoundEndReminderSound(List<Note> chord, int repetitions, int delayTicks) {
        if (!enabled) {
            return;
        }
        for (int i = 0; i < repetitions; i++) {
            soundController.playChordToAll(chord, delayTicks * i);
        }
    }

    public void playEliminationSound(Player player) {
        if (!enabled) {
            return;
        }
        Note note1 = new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 1.059463F);
        Note note2 = new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 0.840896F);
        Note note3 = new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 0.707107F);
        Note note4 = new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 0.529732F);
        soundController.playSound(player, note1);
        soundController.playSound(player, note2, 4);
        soundController.playSound(player, note3, 8);
        soundController.playSound(player, note4, 12);
    }

    public void playWinnerSound(Player player) {
        if (!enabled) {
            return;
        }
        List<Note> chord1 = List.of(
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.529732F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.667420F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.793701F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.943874F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.189207F)
        );
        List<Note> chord2 = List.of(
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.529732F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.707107F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.890899F),
                new Note(Sound.BLOCK_NOTE_BLOCK_FLUTE, 1.059463F)
        );
        soundController.playChord(player, chord1);
        soundController.playChord(player, chord2, 10);
        soundController.playChord(player, chord2, 13);
    }
}
