package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record InvalidColorEvent(Player sender) implements TeamCommandFailEvent {}