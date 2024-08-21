package me.stahu.gsblockshuffle.event;

import me.stahu.gsblockshuffle.event.type.BlockShuffleEvent;

public interface BlockShuffleEventListener<T extends BlockShuffleEvent> {
    void onGameEvent(T event);
}
