package me.stahu.gsblockshuffle.event;


import me.stahu.gsblockshuffle.event.type.BlockShuffleEvent;

import java.util.*;

public class BlockShuffleEventDispatcher {
    private final Map<Class<? extends BlockShuffleEvent>, List<BlockShuffleEventListener<? extends BlockShuffleEvent>>> listeners = new HashMap<>();

    public <T extends BlockShuffleEvent> BlockShuffleEventDispatcher registerListener(Class<T> eventType, BlockShuffleEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        return this;
    }

    public <T extends BlockShuffleEvent> void unregisterListener(Class<T> eventType, BlockShuffleEventListener<T> listener) {
        List<BlockShuffleEventListener<? extends BlockShuffleEvent>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void dispatch(BlockShuffleEvent event) {
        List<BlockShuffleEventListener<? extends BlockShuffleEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (BlockShuffleEventListener<? extends BlockShuffleEvent> listener : eventListeners) {
                dispatchToListener(listener, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockShuffleEvent> void dispatchToListener(BlockShuffleEventListener<? extends BlockShuffleEvent> listener, BlockShuffleEvent event) {
        ((BlockShuffleEventListener<T>) listener).onGameEvent((T) event);
    }
}
