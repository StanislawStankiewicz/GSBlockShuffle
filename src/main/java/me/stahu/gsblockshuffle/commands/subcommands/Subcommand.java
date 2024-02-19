package me.stahu.gsblockshuffle.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

interface Subcommand {

    void parseSubcommand(CommandSender sender, Command command, String label, String[] args);

    List<String> parseTabCompletions(CommandSender sender, Command command, String label, String[] args);
}