package me.stahu.gsblockshuffle.event.type;

import me.stahu.gsblockshuffle.event.GameEvent;

public record InvokeGameStartEvent(int roundStartDelaySeconds) implements GameEvent {
}
