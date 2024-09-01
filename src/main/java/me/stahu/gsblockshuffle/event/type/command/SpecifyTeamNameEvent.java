package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record SpecifyTeamNameEvent(Player sender) implements TeamCommandFailEvent {}
