package me.stahu.gsblockshuffle.event.type.team;

import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;

public record CreateTeamEvent(Player player, Team team) implements TeamEvent {
}
