package me.stahu.gsblockshuffle.event.type.command;

import me.stahu.gsblockshuffle.model.Player;

public record NoSuchTeamEvent(Player sender, String teamName) implements TeamCommandFailEvent {}
