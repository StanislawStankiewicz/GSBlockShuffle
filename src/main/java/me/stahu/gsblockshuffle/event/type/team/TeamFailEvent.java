package me.stahu.gsblockshuffle.event.type.team;

import me.stahu.gsblockshuffle.model.Player;

public record TeamFailEvent(Player player, TeamFailReason reason) implements TeamEvent {
}
