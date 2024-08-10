package me.stahu.gsblockshuffle.config;

/**
 * Enum representing the different modes of block assignment in the game.
 * This enum defines how blocks are assigned to players, teams, or rounds.
 *
 * <p>The available modes are:</p>
 *
 * <ul>
 *   <li>{@link #ONE_PER_PLAYER} - Each player receives a unique block.</li>
 *   <li>{@link #ONE_PER_TEAM} - Each team receives a unique block.</li>
 *   <li>{@link #ONE_PER_GAME} - A single block is assigned for the entire round.</li>
 * </ul>
 *
 * <p>Example configuration usage in a YAML file:</p>
 * <pre>
 * blockAssignmentMode: onePerPlayer # onePerPlayer | onePerTeam | onePerRound
 * </pre>
 *
 * @see me.stahu.gsblockshuffle.config.Config
 */
public enum BlockAssignmentMode {
    ONE_PER_PLAYER,
    ONE_PER_TEAM,
    ONE_PER_GAME;

    public static BlockAssignmentMode fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Block assignment mode cannot be null");
        }

        return switch (name) {
            case "onePerPlayer" -> ONE_PER_PLAYER;
            case "onePerTeam" -> ONE_PER_TEAM;
            case "onePerGame" -> ONE_PER_GAME;
            default -> throw new IllegalArgumentException("Invalid block assignment mode: " + name);
        };
    }
}
