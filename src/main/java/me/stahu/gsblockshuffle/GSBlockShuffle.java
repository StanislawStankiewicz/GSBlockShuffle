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

public final class GSBlockShuffle extends JavaPlugin {
    private File settingsFile;
    private File includedBlocksFile;
    public CategoryTree categoryTree;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        this.includedBlocksFile = this.getDataFolder().toPath().resolve("block_list_categorized.yml").toFile();
        this.createIncludedBlocksFile();

        //load categories configuration
        this.categoryTree = new CategoryTree();
        try {
            categoryTree.parseYaml("plugins\\GSBlockShuffle\\block_list_categorized.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create gui
        SubcategoryGui subcategoryGui = new SubcategoryGui(
                "Block Shuffle Settings",
                null,
                categoryTree.categories.toArray(new Category[0]),
                this);


        GameStateManager gameStateManager = new GameStateManager(settings,this);
        //register events for PlayerListener
        getServer().getPluginManager().registerEvents(new PlayerListener(settings, this, gameStateManager), this);


        BlockShuffleCommand blockShuffleCommand = new BlockShuffleCommand(subcategoryGui, gameStateManager);
        //register commands
        this.getCommand("gsblockshuffle").setExecutor(blockShuffleCommand);
        this.getCommand("gsblockshuffle_set_game_state").setExecutor(blockShuffleCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createSettingsFile() {
        if (!this.settingsFile.exists()) {
            this.saveResource("settings.yml", false);
        }
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
}
