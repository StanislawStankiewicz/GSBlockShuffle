package me.stahu.gsblockshuffle.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.java.Log;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class CategoryTree {

    private List<Category> categories;

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

    public static CategoryTree parseYaml(String filePath) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        FileInputStream inputStream = new FileInputStream(filePath);

        LinkedHashMap<String, Object> map = yaml.load(inputStream);
        ArrayList<Category> categories = new ArrayList<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> categoryMap = (LinkedHashMap<String, Object>) value;

                Category category = new Category(categoryMap, key);
                categories.add(category);
            } else {
                throw new IllegalArgumentException("Unexpected value type: " + value.getClass());
            }
        }
        return new CategoryTree(categories);
    }

    LinkedHashMap<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Category category : categories) {
            map.put(category.getName(), category.toMap());
        }
        return map;
    }
}
