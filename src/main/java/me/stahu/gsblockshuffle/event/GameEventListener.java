package me.stahu.gsblockshuffle.event;

public interface GameEventListener<T extends GameEvent> {
    void onGameEvent(T event);
}
