package me.stahu.gsblockshuffle.game.blocks;

import lombok.RequiredArgsConstructor;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.model.BlockPack;
import me.stahu.gsblockshuffle.model.Category;
import me.stahu.gsblockshuffle.model.CategoryTree;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BlockSelector {

    final Config config;
    final CategoryTree categoryTree;

    public List<BlockPack> getBlocks(int difficulty) {
        List<BlockPack> blocks = new ArrayList<>();
        for (Category category : categoryTree.getCategories()) {
            if (isCategoryIncluded(category, difficulty)) {
                 blocks.addAll(getBlocksRecursive(category, difficulty));
            }
        }
        return blocks;
    }

    private List<BlockPack> getBlocksRecursive(Category category, int difficulty) {
        List<BlockPack> blocks = new ArrayList<>();
        if (category.getBlocks() != null) {
            blocks.addAll(category.getBlocks().stream()
                    .map(block -> new BlockPack(List.of(block)))
                    .toList());
        }
        if (category.getSubcategories() != null) {
            for (Category subcategory : category.getSubcategories()) {
                if (isCategoryIncluded(subcategory, difficulty)) {
                    blocks.addAll(getBlocksRecursive(subcategory, difficulty));
                }
            }
        }
        return blocks;
    }

    private boolean isCategoryIncluded(Category category, int difficulty) {
        boolean included = category.isIncluded();
        boolean includeLowerDifficulties = config.isIncludeLowerDifficulties();
        boolean includeVariants = config.isIncludeVariants();

        boolean difficultyMatch;
        if (includeLowerDifficulties) {
            difficultyMatch = category.getDifficulty() <= difficulty;
        } else {
            difficultyMatch = category.getDifficulty() == difficulty;
        }

        boolean variantMatch;
        if (includeVariants) {
            variantMatch = true;
        } else {
            variantMatch = !category.getName().equals("variant");
        }

        return included && difficultyMatch && variantMatch;
    }
}
