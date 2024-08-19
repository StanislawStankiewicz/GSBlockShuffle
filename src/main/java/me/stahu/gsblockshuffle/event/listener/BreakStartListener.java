package me.stahu.gsblockshuffle.event.listener;

import me.stahu.gsblockshuffle.event.GameEventListener;
import me.stahu.gsblockshuffle.event.type.RoundBreakEvent;
import me.stahu.gsblockshuffle.event.type.BreakStartEvent;

public class BreakStartListener implements GameEventListener<BreakStartEvent> {

public class RoundBreakListener implements GameEventListener<RoundBreakEvent> {
    @Override
    public void onGameEvent(RoundBreakEvent event) {
        // currently no usage
    public void onGameEvent(BreakStartEvent event) {
    }
}
