package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record AlreadyInTeamEvent(Player sender) implements TeamCommandFailEvent {}
