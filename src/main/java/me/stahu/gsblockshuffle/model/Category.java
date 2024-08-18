package me.stahu.gsblockshuffle.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

@Getter
@EqualsAndHashCode
public class Category {

    String name;
    boolean isIncluded;
    int difficulty;
    List<Category> subcategories;
    List<Block> blocks;

    public Category(String name, boolean isIncluded, int difficulty, List<Category> subcategories, List<Block> blocks) {
        this.name = name;
        this.isIncluded = isIncluded;
        this.difficulty = difficulty;
        this.subcategories = subcategories;
        this.blocks = blocks;
    }

    public Category(Map<String, Object> stringObjectMap, String key) {
        this.name = key;
        fromMap(stringObjectMap);
    }

    public Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("isIncluded", isIncluded);
        map.put("difficulty", difficulty);
        if (!subcategories.isEmpty()) {
            for (Category subcategory : subcategories) {
                map.put(subcategory.name, subcategory.toMap());
            }
            return map;
        }
        ArrayList<ArrayList<String>> elements = new ArrayList<>();
        for (Block block : blocks) {
            elements.add(new ArrayList<>(block.names()));
        }
        map.put("elements", elements);
        return map;
    }

    void fromMap(Map<String, Object> map) {
        this.isIncluded = map.get("isIncluded") != null && (boolean) map.get("isIncluded");
        this.difficulty = map.get("difficulty") != null ? (int) map.get("difficulty") : 0;
        map.remove("isIncluded");
        map.remove("difficulty");
        if (map.containsKey("elements")) {
            this.blocks = new ArrayList<>();
            for (ArrayList<String> element : castElements((ArrayList<Object>) map.get("elements"))) {
                this.blocks.add(new Block(element));
            }
        } else {
            this.subcategories = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Category subcategory = new Category((LinkedHashMap<String, Object>) entry.getValue(), entry.getKey());
                this.subcategories.add(subcategory);
            }
        }
    }

    ArrayList<ArrayList<String>> castElements(ArrayList<Object> elements) {
        ArrayList<ArrayList<String>> castedElements = new ArrayList<>();
        for (Object o : elements) {
            if (o instanceof String s) {
                ArrayList<String> element = new ArrayList<>();
                element.add(s);
                castedElements.add(element);
            } else {
                castedElements.add((ArrayList<String>) o);
            }
        }
        return castedElements;
    }
}
