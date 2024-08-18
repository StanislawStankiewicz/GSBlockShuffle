package me.stahu.gsblockshuffle.event;


import java.util.*;

public class GameEventDispatcher {
    private final Map<Class<? extends GameEvent>, List<GameEventListener<? extends GameEvent>>> listeners = new HashMap<>();

    public <T extends GameEvent> GameEventDispatcher registerListener(Class<T> eventType, GameEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        return this;
    }

    public <T extends GameEvent> void unregisterListener(Class<T> eventType, GameEventListener<T> listener) {
        List<GameEventListener<? extends GameEvent>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void dispatch(GameEvent event) {
        List<GameEventListener<? extends GameEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (GameEventListener<? extends GameEvent> listener : eventListeners) {
                dispatchToListener(listener, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends GameEvent> void dispatchToListener(GameEventListener<? extends GameEvent> listener, GameEvent event) {
        ((GameEventListener<T>) listener).onGameEvent((T) event);
    }
}
