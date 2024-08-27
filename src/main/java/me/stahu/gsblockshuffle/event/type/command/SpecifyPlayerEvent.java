package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record SpecifyPlayerEvent(Player sender) implements TeamCommandFailEvent {}
