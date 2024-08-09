package me.stahu.gsblockshuffle.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTests {

    @Test
    void getBlocks_returnsAllBlocks() {
        List<Block> blocks = List.of(new Block("block1"), new Block("block2"));
        List<Block> subcategoryBlocks = List.of(new Block("block3"), new Block("block4"));

        Category subcategory = new Category("Subcategory", true, 1, new ArrayList<>(), subcategoryBlocks);
        Category category = new Category("Category", true, 1, List.of(subcategory), blocks);

        List<Block> result = category.getBlocks();
        List<Block> expectedBlocks = List.of(
                new Block("block1"),
                new Block("block2"),
                new Block("block3"),
                new Block("block4")
        );

        assertThat(expectedBlocks).containsExactlyInAnyOrder(result.toArray(new Block[0]));
    }

    @Test
    void getBlocks_returnsAllIncludedBlocks() {
        List<Block> blocks = List.of(new Block("block1"), new Block("block2"));
        List<Block> subcategoryBlocks1 = List.of(new Block("block3"), new Block("block4"));
        List<Block> subcategoryBlocks2 = List.of(new Block("block5"), new Block("block6"));

        Category subcategory1 = new Category("Subcategory", false, 1, new ArrayList<>(), subcategoryBlocks1);
        Category subcategory2 = new Category("Subcategory", true, 1, new ArrayList<>(), subcategoryBlocks2);
        Category category = new Category("Category", true, 1, List.of(subcategory1, subcategory2), blocks);

        List<Block> result = category.getBlocks();
        List<Block> expectedBlocks = List.of(
                new Block("block1"),
                new Block("block2"),
                new Block("block5"),
                new Block("block6")
        );

        assertEquals(expectedBlocks.size(), result.size());
        assertTrue(expectedBlocks.containsAll(result));
    }
}
