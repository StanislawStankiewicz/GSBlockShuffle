package me.stahu.gsblockshuffle.settings;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Loader {
    public static void parseYaml(String filePath, JavaPlugin plugin, CategoryTree categoryTree) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(plugin.getResource(filePath));
        ArrayList<Category> categories = new ArrayList<Category>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Category category = new Category((Map<String, Object>) entry.getValue(), entry.getKey());
            categories.add(category);
        }
        categoryTree.categories = categories;
    }

    public static void dumpYaml(String filePath, JavaPlugin plugin, CategoryTree categoryTree) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = categoryTree.toMap();
        String output = yaml.dump(map);
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
}}