package me.stahu.gsblockshuffle.game.blocks;

import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.Block;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Category;
import me.stahu.gsblockshuffle.model.CategoryTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BlockSelectorTests {

    private Config config;
    private CategoryTree categoryTree;
    private BlockSelector blockSelector;

    @BeforeEach
    void setUp() {
        config = Mockito.mock(Config.class);
        categoryTree = Mockito.mock(CategoryTree.class);
        blockSelector = new BlockSelector(config, categoryTree);
    }

    private Category createMockCategory(String name, int difficulty, boolean isIncluded, List<Block> blocks, List<Category> subcategories) {
        Category category = Mockito.mock(Category.class);
        when(category.getName()).thenReturn(name);
        when(category.getDifficulty()).thenReturn(difficulty);
        when(category.isIncluded()).thenReturn(isIncluded);
        when(category.getBlocks()).thenReturn(blocks);
        when(category.getSubcategories()).thenReturn(subcategories);
        return category;
    }

    @Test
    void getBlocks_ReturnsCorrectBlocksForGivenDifficulty() {
        Category category = createMockCategory("category", 1, true, List.of(new Block("block1")), List.of());
        when(categoryTree.getCategories()).thenReturn(List.of(category));
        when(config.isIncludeLowerDifficulties()).thenReturn(true);
        when(config.isIncludeVariants()).thenReturn(true);

        List<BlockPack> blocks = blockSelector.getBlocks(1);

        assertEquals(1, blocks.size());
    }

    @Test
    void getBlocks_ExcludesCategoriesNotMatchingDifficulty() {
        Category category = createMockCategory("category", 2, true, List.of(new Block("block1"), new Block("block2")), List.of());
        when(categoryTree.getCategories()).thenReturn(List.of(category));
        when(config.isIncludeLowerDifficulties()).thenReturn(false);
        when(config.isIncludeVariants()).thenReturn(true);

        List<BlockPack> blocks = blockSelector.getBlocks(1);

        assertEquals(0, blocks.size());
    }

    @Test
    void getBlocks_IncludesSubcategoriesMatchingDifficulty() {
        Category subcategory = createMockCategory("subcategory", 1, true, List.of(new Block("block1"), new Block("block2")), List.of());
        Category category = createMockCategory("category", 1, true, List.of(new Block("block1"), new Block("block2")), List.of(subcategory));
        when(categoryTree.getCategories()).thenReturn(List.of(category));
        when(config.isIncludeLowerDifficulties()).thenReturn(true);
        when(config.isIncludeVariants()).thenReturn(true);

        List<BlockPack> blocks = blockSelector.getBlocks(1);

        assertEquals(4, blocks.size());
    }

    @Test
    void getBlocks_ExcludesVariantCategoriesWhenIncludeVariantsIsFalse() {
        Category category = createMockCategory("variant", 1, true, List.of(new Block("block1"), new Block("block2")), List.of());
        when(categoryTree.getCategories()).thenReturn(List.of(category));
        when(config.isIncludeLowerDifficulties()).thenReturn(true);
        when(config.isIncludeVariants()).thenReturn(false);

        List<BlockPack> blocks = blockSelector.getBlocks(1);

        assertEquals(0, blocks.size());
    }
}
