package me.stahu.gsblockshuffle.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Block {
    private final List<String> names;

    public Block(String name) {
        this.names = List.of(name);
    }

    public Block(String... names) {
        this.names = List.of(names);
    }
}