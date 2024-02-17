package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.commands.BlockShuffleCommand;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.event.PlayerListener;
import me.stahu.gsblockshuffle.gui.page.SubcategoryGui;
import me.stahu.gsblockshuffle.settings.Category;
import me.stahu.gsblockshuffle.settings.CategoryTree;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class GSBlockShuffle extends JavaPlugin {
    private File settingsFile;
    private File includedBlocksFile;
    public CategoryTree categoryTree;
    private GameStateManager gameStateManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);
        System.out.println(settings.getString("roundsPerGame"));

        this.includedBlocksFile = this.getDataFolder().toPath().resolve("block_list_categorized.yml").toFile();
        this.createIncludedBlocksFile();

        // TODO get path of the yml file when creating it
        //load categories configuration
        this.categoryTree = new CategoryTree();
        try {
            categoryTree.parseYaml(includedBlocksFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create gui
        SubcategoryGui subcategoryGui = new SubcategoryGui(
                "Block Shuffle Settings",
                null,
                categoryTree.categories.toArray(new Category[0]),
                this);

        // TODO setting this up immediately is inefficient
        gameStateManager= new GameStateManager(settings, this);
        //register events for PlayerListener
        getServer().getPluginManager().registerEvents(new PlayerListener(settings, this, gameStateManager), this);

        BlockShuffleCommand blockShuffleCommand = new BlockShuffleCommand(subcategoryGui, gameStateManager, settings, this);
        //register commands
        this.getCommand("gsblockshuffle").setExecutor(blockShuffleCommand);

        try {
            settings.save("savedSettings.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        gameStateManager.clearScoreboard();
        gameStateManager.clearBossBar();
    }

    private void createSettingsFile() {
        if (!this.settingsFile.exists()) {
            this.saveResource("settings.yml", false);
        }
    }

    public void saveSettings(YamlConfiguration settings) throws IOException {
        // TODO this doesnt work
        settings.save(this.settingsFile.toString());
    }

    private void createIncludedBlocksFile() {
        try {
            if (!this.includedBlocksFile.exists()) {
                this.saveResource("block_list_categorized.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error here
        }
    }

    public void saveConfiguration() {
        this.categoryTree.saveConfiguration();
    }

    // TODO check if this is the best way to handle this
    public void changeSetting(YamlConfiguration settings, String key, String value) throws IllegalArgumentException {
        if (settings.isBoolean(key)) {
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                settings.set(key, Boolean.parseBoolean(value));
            } else {
                throw new IllegalArgumentException("Value must be true or false");
            }
        } else if (settings.isInt(key)) {
            if(value.matches("\\d+")) {
                settings.set(key, Integer.parseInt(value));
            } else {
                throw new IllegalArgumentException("Value must be an integer");
            }
        } else {
            settings.set(key, value);
        }
    }
}
