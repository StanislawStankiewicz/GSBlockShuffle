package me.stahu.gsblockshuffle.event.type.game;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;


public record BlockAssignEvent(Player player, Block block) implements GameEvent {
}
