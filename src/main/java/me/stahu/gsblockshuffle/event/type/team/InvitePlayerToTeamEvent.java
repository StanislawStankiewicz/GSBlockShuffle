package me.stahu.gsblockshuffle.event.type.team;

import me.stahu.gsblockshuffle.model.Player;

public record InvitePlayerToTeamEvent(Player inviter, Player player) implements TeamEvent {
}
