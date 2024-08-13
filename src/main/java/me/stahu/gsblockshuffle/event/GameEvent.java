package me.stahu.gsblockshuffle.event;


import me.stahu.gsblockshuffle.config.Config;

public record GameEvent(GameEventType type, Config config) {
}