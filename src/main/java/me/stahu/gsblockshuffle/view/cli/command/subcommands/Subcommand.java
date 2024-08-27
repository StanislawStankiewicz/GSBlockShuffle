package me.stahu.gsblockshuffle.view.cli.command.subcommands;

import me.stahu.gsblockshuffle.model.Player;

import java.util.List;

interface Subcommand {
    void parseSubcommand(Player sender, String[] args);

    List<String> parseTabCompletions(Player sender, String[] args);
}