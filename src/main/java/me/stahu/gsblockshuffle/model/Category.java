package me.stahu.gsblockshuffle.model;

import lombok.Getter;

import java.util.*;

@Getter
public class Category {

    String name;
    boolean isIncluded;
    int difficulty;
    Set<Category> subcategories;
    Set<Block> blocks;

    public Category(String name, boolean isIncluded, int difficulty, Set<Category> subcategories, Set<Block> blocks) {
        this.name = name;
        this.isIncluded = isIncluded;
        this.difficulty = difficulty;
        this.subcategories = subcategories;
        this.blocks = blocks;
    }

    public Set<Block> getBlocks() {
        Set<Block> result = new HashSet<>();
        for (Category subcategory : subcategories) {
            if (subcategory.isIncluded) {
                result.addAll(subcategory.getBlocks());
            }
        }
        result.addAll(this.blocks);
        return result;
    }

    public Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("isIncluded", isIncluded);
        map.put("difficulty", difficulty);
        if (!subcategories.isEmpty()) {
            for (Category subcategory : subcategories) {
                map.put(subcategory.name, subcategory.toMap());
            }
        } else {
            ArrayList<ArrayList<String>> elements = new ArrayList<>();
            for (Block block : blocks) {
                elements.add(new ArrayList<>(block.getNames()));
            }
            map.put("elements", elements);
        }
        return map;
    }
}
