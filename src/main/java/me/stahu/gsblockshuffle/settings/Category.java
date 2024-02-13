package me.stahu.gsblockshuffle.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public Category(LinkedHashMap<String, Object> map, String name) {
        fromMap(map, name);
    }

    public ArrayList<ArrayList<String>> getBlocks() {
        ArrayList<ArrayList<String>> blocks = new ArrayList<>();

        if(elements != null) {
            blocks.addAll(elements);
        } else {
            for (Category subcategory : subCategories) {
                blocks.addAll(subcategory.getBlocks());
            }
        }
        return blocks;
    }

    public LinkedHashMap<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
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

    // TODO safe cast
    public void fromMap(Map<String, Object> map, String name) {
        this.name = name;
        this.isIncluded = map.get("isIncluded") != null && (boolean) map.get("isIncluded");
        this.difficulty = map.get("difficulty") != null ? (int) map.get("difficulty") : 0;
        map.remove("isIncluded");
        map.remove("difficulty");
        if (map.containsKey("elements")) {
//            this.elements = (ArrayList<ArrayList<String>>) map.get("elements");
            this.elements = castElements((ArrayList<Object>) map.get("elements"));
        } else {
            this.subCategories = new ArrayList<Category>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Category subcategory = new Category((LinkedHashMap<String, Object>) entry.getValue(), entry.getKey());
                this.subCategories.add(subcategory);
            }
        }
    }

    // TODO raise error if neither String or ArrayList
    private ArrayList<ArrayList<String>> castElements(ArrayList<Object> elements){
        ArrayList<ArrayList<String>> castedElements = new ArrayList<>();
        //check if element is string or list
        for (Object o : elements) {
            if (o instanceof String) {
                ArrayList<String> element = new ArrayList<>();
                element.add((String) o);
                castedElements.add(element);
            } else {
                castedElements.add((ArrayList<String>) o);
            }
        }
        return castedElements;
    }

    public void setIncluded(boolean included) {
        this.isIncluded = included;
    }
}

