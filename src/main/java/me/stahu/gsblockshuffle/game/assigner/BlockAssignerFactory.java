package me.stahu.gsblockshuffle.game.assigner;

import lombok.NoArgsConstructor;
import me.stahu.gsblockshuffle.config.Config;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BlockAssignerFactory {

    public static BlockAssigner getBlockAssigner(Config config) {
        return switch (config.getBlockAssignmentMode()) {
            case ONE_PER_GAME -> new GameBlockAssigner();
            case ONE_PER_TEAM -> new TeamBlockAssigner();
            case ONE_PER_PLAYER -> new PlayerBlockAssigner();
        };
    }
}
