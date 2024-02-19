package me.stahu.gsblockshuffle.commands;

import java.util.List;

public class CommandBase {
    protected List<String> filterCompletions(List<String> completions, String arg) {
        return completions.stream().filter(completion -> completion.startsWith(arg)).toList();
    }
}
