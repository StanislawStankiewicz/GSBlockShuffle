package me.stahu.gsblockshuffle.settings;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collection;
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

    // TODO refine code
    public ArrayList<ArrayList<ArrayList<String>>> getBlockSet(YamlConfiguration settings) {
        ArrayList<ArrayList<ArrayList<String>>> blocks = new ArrayList<>();
        boolean includeLowerDifficulties = settings.getBoolean("includeLowerDifficulties");
        boolean treatAllAsIndividualBlocks = settings.getBoolean("treatAllAsIndividualBlocks");

        if (!isIncluded || !checkDifficulty(settings, true)) {
            return null;
        }

        if (treatAllAsIndividualBlocks) {
            if (elements != null) {
                for (ArrayList<String> element : elements) {
                    ArrayList<ArrayList<String>> block = new ArrayList<>();
                    block.add(element);
                    blocks.add(block);
                }
            } else {
                for (Category subcategory : subCategories) {
                    ArrayList<ArrayList<ArrayList<String>>> blockSet = subcategory.getBlockSet(settings);
                    if (blockSet != null) {
                        blocks.addAll(blockSet);
                    }
                }
            }
            return blocks;
        }
        // Group blocks into packs like variants of wool, conrete, etc.
        // Handle variants
        if (subCategories != null) {
            // This pack is used to group all "base" and "variant" subcategories to a group that will be treated as a single block
            ArrayList<ArrayList<String>> pack = new ArrayList<>();
            for (Category subcategory : subCategories) {
                // Only add "base" and "variant" subcategories + check difficulty and isIncluded
                if ((subcategory.name.equals("base") || subcategory.name.equals("variant")) && subcategory.isIncluded
                        && subcategory.checkDifficulty(settings, includeLowerDifficulties)) {
                    pack.addAll(subcategory.elements);
                } else {
                    ArrayList<ArrayList<ArrayList<String>>> blockSet = subcategory.getBlockSet(settings);
                    if (blockSet != null) {
                        blocks.addAll(blockSet);
                    }
                    if (subcategory.elements != null && subcategory.isIncluded
                            && subcategory.checkDifficulty(settings, includeLowerDifficulties)) {
                        for (ArrayList<String> element : subcategory.elements) {
                            ArrayList<ArrayList<String>> wrap = new ArrayList<>();
                            wrap.add(element);
                            blocks.add(wrap);
                        }
                    }
                }
            }
            if (!pack.isEmpty()) {
                blocks.add(pack);
            }
        }
        return blocks;
    }

    /**
     * Get all blocks under this category.
     *
     * @return ArrayList of blocks
     */
    public ArrayList<ArrayList<String>> getBlocks() {
        ArrayList<ArrayList<String>> blocks = new ArrayList<>();

        if (elements != null) {
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
            this.subCategories = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Category subcategory = new Category((LinkedHashMap<String, Object>) entry.getValue(), entry.getKey());
                this.subCategories.add(subcategory);
            }
        }
    }

    // TODO raise error if neither String or ArrayList
    private ArrayList<ArrayList<String>> castElements(ArrayList<Object> elements) {
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

    private boolean checkDifficulty(YamlConfiguration settings, boolean includeLowerDifficulties) {
        int gameDifficulty = settings.getInt("difficulty");

        if (gameDifficulty == -1) {
            return true;
        }

        if (includeLowerDifficulties) {
            return this.difficulty <= gameDifficulty;
        }
        return this.difficulty == gameDifficulty;
    }

    public Collection<String> getAllBlocks() {
        ArrayList<String> blocks = new ArrayList<>();

        if (elements != null) {
            for (ArrayList<String> element : elements) {
                blocks.addAll(element);
            }
        } else {
            for (Category subcategory : subCategories) {
                blocks.addAll(subcategory.getAllBlocks());
            }
        }

        return blocks;
    }
}

