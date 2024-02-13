package me.stahu.gsblockshuffle.settings;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CategoryTree {
    public ArrayList<Category> categories;

    public CategoryTree() { }

    public LinkedHashMap<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Category category : categories) {
            map.put(category.name, category.toMap());
        }
        return map;
    }

    public void parseYaml(String filePath)
            throws FileNotFoundException {
        Yaml yaml = new Yaml();

        FileInputStream inputStream = new FileInputStream(filePath);

        LinkedHashMap<String, Object> map = yaml.load(inputStream);
        ArrayList<Category> categories = new ArrayList<Category>();
        for (String key : map.keySet()) {
            //TODO safe cast
            Category category = new Category((LinkedHashMap<String, Object>) map.get(key), key);
            categories.add(category);
        }
        this.categories = categories;
    }

    public void dumpYaml(String filePath) {
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> map = this.toMap();
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