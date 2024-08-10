package me.stahu.gsblockshuffle.config;

/**
 * Enum representing different teleportation modes in the game.
 *
 * <p>The available modes are:</p>
 * <ul>
 *   <li>{@link #DISABLED} - Teleportation is completely disabled.</li>
 *   <li>{@link #AMOUNT_PER_PLAYER} - Teleportation is allowed a limited number of times per player.</li>
 *   <li>{@link #AMOUNT_PER_TEAM} - Teleportation is allowed a limited number of times per team.</li>
 *   <li>{@link #UNLIMITED} - Teleportation is unlimited.</li>
 * </ul>
 *
 * @see me.stahu.gsblockshuffle.config.Config
 */
public enum TeleportMode {
    DISABLED,
    AMOUNT_PER_PLAYER,
    AMOUNT_PER_TEAM,
    UNLIMITED;

    public static TeleportMode fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Teleport mode cannot be null");
        }
        return switch (name) {
            case "disabled" -> DISABLED;
            case "amountPerPlayer" -> AMOUNT_PER_PLAYER;
            case "amountPerTeam" -> AMOUNT_PER_TEAM;
            case "unlimited" -> UNLIMITED;
            default -> throw new IllegalArgumentException("Invalid teleport mode: " + name);
        };
    }
}
