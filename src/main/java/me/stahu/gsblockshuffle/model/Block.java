package me.stahu.gsblockshuffle.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;


@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class Block {
    private final Set<String> names;

    public Block(String name) {
        this.names = Set.of(name);
    }

    public Block(String... names) {
        this.names = Set.of(names);
    }
}