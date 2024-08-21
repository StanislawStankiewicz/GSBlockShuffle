package me.stahu.gsblockshuffle.event.type.team;

import me.stahu.gsblockshuffle.model.Team;

public record RemoveTeamEvent(Team team) implements TeamEvent {
}
