package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record NoSuchPlayerEvent(Player sender, String playerName) implements TeamCommandFailEvent {}
