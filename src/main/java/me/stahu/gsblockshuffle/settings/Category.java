package me.stahu.gsblockshuffle.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Category {
    public String name;
    public boolean isIncluded;
    public int difficulty;
    public ArrayList<Category> subCategories;
    public ArrayList<ArrayList<String>> elements;

    public Category(String name, boolean isIncluded, int difficulty, ArrayList<Category> subcategories) {
        this.name = name;
        this.isIncluded = isIncluded;
        this.difficulty = difficulty;
        this.subCategories = subcategories;
    }

    public Category(Map<String, Object> map, String name) {
        fromMap(map, name);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("isIncluded", isIncluded);
        map.put("difficulty", difficulty);
        if (subCategories != null) {
            for (Category subcategory : subCategories) {
                map.put(subcategory.name, subcategory.toMap());
            }
        } else {
            map.put("elements", elements);
        }
        return map;
    }

    public void fromMap(Map<String, Object> map, String name) {
        this.name = name;
        this.isIncluded = map.get("included") != null && (boolean) map.get("included");
        this.difficulty = map.get("difficulty") != null ? (int) map.get("difficulty") : 0;
        map.remove("included");
        map.remove("difficulty");
        if (map.containsKey("elements")) {
            this.elements = (ArrayList<ArrayList<String>>) map.get("elements");
        } else {
            this.subCategories = new ArrayList<Category>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Category subcategory = new Category((Map<String, Object>) entry.getValue(), entry.getKey());
                this.subCategories.add(subcategory);
            }
        }
    }
}

