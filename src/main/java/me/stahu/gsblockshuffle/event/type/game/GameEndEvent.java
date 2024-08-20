package me.stahu.gsblockshuffle.event.type.game;

import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public record GameEndEvent(Set<Team> teams) implements GameEvent {
}
