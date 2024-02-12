package me.stahu.gsblockshuffle.settings;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryTree {
    public ArrayList<Category> categories;

    public CategoryTree() { }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Category category : categories) {
            map.put(category.name, category.toMap());
        }
        return map;
    }

    public void parseYaml(String filePath)
            throws FileNotFoundException {
        Yaml yaml = new Yaml();

        FileInputStream inputStream = new FileInputStream(filePath);

        Map<String, Object> map = yaml.load(inputStream);
        ArrayList<Category> categories = new ArrayList<Category>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //TODO safe cast
            Category category = new Category((Map<String, Object>) entry.getValue(), entry.getKey());
            categories.add(category);
        }
        this.categories = categories;
    }

    public static void dumpYaml(String filePath, CategoryTree categoryTree) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = categoryTree.toMap();
        String output = yaml.dump(map);
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            // TODO Implement a logger
            e.printStackTrace();
        }
    }
}