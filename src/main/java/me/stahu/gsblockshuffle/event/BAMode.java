package me.stahu.gsblockshuffle.event;


public enum BAMode {
    ONE_PER_PLAYER, ONE_PER_TEAM, ONE_PER_ROUND;

    public static BAMode fromString(String mode) {
        return switch (mode.toLowerCase()) {
            case "oneperplayer" -> ONE_PER_PLAYER;
            case "oneperteam" -> ONE_PER_TEAM;
            case "oneperround" -> ONE_PER_ROUND;
            default -> throw new IllegalArgumentException("Invalid BlockAssignmentMode: " + mode);
        };
    }
}
