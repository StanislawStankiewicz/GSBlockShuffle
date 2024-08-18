package me.stahu.gsblockshuffle.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockTests {

    @Test
    void equals_returnsTrueForEqualBlocks() {
        Block block1 = new Block("block1", "block2");
        Block block2 = new Block("block1", "block2");

        assertEquals(block1, block2);
    }
}
