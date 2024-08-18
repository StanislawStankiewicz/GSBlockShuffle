package me.stahu.gsblockshuffle.event.type;

import me.stahu.gsblockshuffle.event.GameEvent;
import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public record GameEndEvent(Set<Team> teams) implements GameEvent {
}
