package me.stahu.gsblockshuffle.game.eliminator;

import me.stahu.gsblockshuffle.model.Team;

import java.util.Set;

public interface TeamEliminationStrategy {
    void eliminateTeams(Set<Team> teams);
}
