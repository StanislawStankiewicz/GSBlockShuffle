package me.stahu.gsblockshuffle.event;


import java.util.ArrayList;
import java.util.List;

public class GameEventDispatcher {
    private final List<GameEventListener> listeners = new ArrayList<>();

    public void registerListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void dispatch(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }
}
