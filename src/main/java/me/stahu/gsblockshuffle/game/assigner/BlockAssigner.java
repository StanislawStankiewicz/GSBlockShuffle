package me.stahu.gsblockshuffle.game.assigner;

import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.Team;

import java.util.List;
import java.util.Random;
import java.util.Set;

public interface BlockAssigner {

    Random random = new Random();

    void assignBlocks(Set<Team> players, List<Block> blocks);
}
