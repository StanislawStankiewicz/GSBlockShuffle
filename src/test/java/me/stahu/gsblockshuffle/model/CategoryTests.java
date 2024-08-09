package me.stahu.gsblockshuffle.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTests {

    @Test
    void getBlocks_returnsAllBlocks() {
        Set<Block> blocks = Set.of(new Block("block1"), new Block("block2"));
        Set<Block> subcategoryBlocks = Set.of(new Block("block3"), new Block("block4"));

        Category subcategory = new Category("Subcategory", true, 1, new HashSet<>(), subcategoryBlocks);
        Category category = new Category("Category", true, 1, Set.of(subcategory), blocks);

        Set<Block> result = category.getBlocks();
        Set<Block> expectedBlocks = Set.of(
                new Block("block1"),
                new Block("block2"),
                new Block("block3"),
                new Block("block4")
        );

        assertThat(expectedBlocks).containsExactlyInAnyOrder(result.toArray(new Block[0]));
    }

    @Test
    void getBlocks_returnsAllIncludedBlocks() {
        Set<Block> blocks = Set.of(new Block("block1"), new Block("block2"));
        Set<Block> subcategoryBlocks1 = Set.of(new Block("block3"), new Block("block4"));
        Set<Block> subcategoryBlocks2 = Set.of(new Block("block5"), new Block("block6"));

        Category subcategory1 = new Category("Subcategory", false, 1, new HashSet<>(), subcategoryBlocks1);
        Category subcategory2 = new Category("Subcategory", true, 1, new HashSet<>(), subcategoryBlocks2);
        Category category = new Category("Category", true, 1, Set.of(subcategory1, subcategory2), blocks);

        Set<Block> result = category.getBlocks();
        Set<Block> expectedBlocks = Set.of(
                new Block("block1"),
                new Block("block2"),
                new Block("block5"),
                new Block("block6")
        );

        assertEquals(expectedBlocks.size(), result.size());
        assertTrue(expectedBlocks.containsAll(result));
    }
}
