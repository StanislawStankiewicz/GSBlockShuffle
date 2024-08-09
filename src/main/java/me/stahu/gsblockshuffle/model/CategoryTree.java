package me.stahu.gsblockshuffle.model;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Log
@AllArgsConstructor
public class CategoryTree {

    private List<Category> categories;

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (Category category : categories) {
            if (category.isIncluded()) {
                blocks.addAll(category.getBlocks());
            }
        }
        return blocks;
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
            log.log(java.util.logging.Level.SEVERE, "Failed to write to file", e);
        }
    }

    LinkedHashMap<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Category category : categories) {
            map.put(category.getName(), category.toMap());
        }
        return map;
    }
}
