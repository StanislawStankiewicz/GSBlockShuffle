package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record NoSuchInviteEvent(Player sender) implements TeamCommandFailEvent {}
