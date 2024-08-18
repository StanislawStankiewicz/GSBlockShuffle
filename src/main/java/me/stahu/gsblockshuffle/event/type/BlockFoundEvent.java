package me.stahu.gsblockshuffle.event.type;

import me.stahu.gsblockshuffle.event.GameEvent;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Player;

public record BlockFoundEvent(Player player, Block block) implements GameEvent {
}
