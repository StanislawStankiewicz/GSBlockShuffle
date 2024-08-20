package me.stahu.gsblockshuffle.event.type.team;

import me.stahu.gsblockshuffle.model.Player;

public record KickFromTeamEvent(Player leader, Player player) implements TeamEvent {
}
