package me.stahu.gsblockshuffle.event.type;

import me.stahu.gsblockshuffle.event.GameEvent;

public record BreakStartEvent(int roundStartDelaySeconds) implements GameEvent {
}
