package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public class NoEliminationStrategy implements TeamEliminationStrategy {
    @Override
    public void eliminateTeams(Set<Team> teams) {
        // do nothing
    }
}
