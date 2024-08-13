package me.stahu.gsblockshuffle.model;

import java.util.List;


public record Block(List<String> names) {

    public Block(String name) {
        this(List.of(name));
    }

    public Block(String... names) {
        this(List.of(names));
    }
}