package me.stahu.gsblockshuffle.settings;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * The CategoryTree class represents a tree of categories.
 * It is used to store and manage the categories of blocks.
 */
public class CategoryTree {
    public ArrayList<Category> categories;

    /**
     * The constructor for the CategoryTree class.
     */
    public CategoryTree() {
    }

    /**
     * Method to convert the CategoryTree to a LinkedHashMap.
     *
     * @return LinkedHashMap<String, Object>
     */
    public LinkedHashMap<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Category category : categories) {
            map.put(category.name, category.toMap());
        }
        return map;
    }

    /**
     * Method to parse a YAML file and create a CategoryTree from it.
     *
     * @param filePath The path to the YAML file.
     * @throws FileNotFoundException If the file is not found.
     */
    public void parseYaml(String filePath)
            throws FileNotFoundException {
        Yaml yaml = new Yaml();

        FileInputStream inputStream = new FileInputStream(filePath);

        LinkedHashMap<String, Object> map = yaml.load(inputStream);
        ArrayList<Category> categories = new ArrayList<>();
        for (String key : map.keySet()) {
            //TODO safe cast
            Category category = new Category((LinkedHashMap<String, Object>) map.get(key), key);
            categories.add(category);
        }
        this.categories = categories;
    }

    /**
     * Method to dump the CategoryTree to a YAML file.
     *
     * @param filePath The path to the YAML file.
     */
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

    public ArrayList<ArrayList<ArrayList<String>>> getBlockList(YamlConfiguration settings) {
        ArrayList<ArrayList<ArrayList<String>>> blocks = new ArrayList<>();

        for (Category category : categories) {
            ArrayList<ArrayList<ArrayList<String>>> blockList = category.getBlockSet(settings);
            if (blockList != null) {
                if(!blockList.isEmpty()){
                    blocks.addAll(blockList);
                }
            }
        }

        return blocks;
    }

    public ArrayList<String> getRandomBlock(YamlConfiguration settings) {
        Random random = new Random();

        ArrayList<ArrayList<ArrayList<String>>> blockList = getBlockList(settings);

        if(blockList.isEmpty()) {
            throw new IllegalArgumentException("Block list is empty");
        }

        ArrayList<ArrayList<String>> block = blockList.get(random.nextInt(blockList.size()));

        // Return random variant of the block
        return block.get(random.nextInt(block.size()));
    }

    /**
     * Save the configuration to "plugins\GSBlockShuffle\block_list_categorized.yml"
     */
    public void saveConfiguration() {
        this.dumpYaml("plugins\\GSBlockShuffle\\block_list_categorized.yml");
    }
}