package me.stahu.gsblockshuffle.game.assigner;

import lombok.NoArgsConstructor;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;

@NoArgsConstructor(access = lombok.AccessLevel.NONE)
public class BlockAssignerFactory {

    public static BlockAssigner getBlockAssigner(Config config, BlockShuffleEventDispatcher dispatcher) {
        return switch (config.getBlockAssignmentMode()) {
            case ONE_PER_GAME -> new GameBlockAssigner(dispatcher);
            case ONE_PER_TEAM -> new TeamBlockAssigner(dispatcher);
            case ONE_PER_PLAYER -> new PlayerBlockAssigner(dispatcher);
        };
    }
}
