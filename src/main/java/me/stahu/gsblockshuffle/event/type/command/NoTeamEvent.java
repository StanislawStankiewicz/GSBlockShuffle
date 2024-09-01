package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record NoTeamEvent(Player sender) implements TeamCommandFailEvent {}
