package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record SpecifyColorEvent(Player sender) implements TeamCommandFailEvent {}
