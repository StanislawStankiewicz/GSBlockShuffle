package me.stahu.gsblockshuffle.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryTree {
    public ArrayList<Category> categories;

    public CategoryTree() {
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        for (Category category : categories) {
            map.put(category.name, category.toMap());
        }
        return map;
    }
}
