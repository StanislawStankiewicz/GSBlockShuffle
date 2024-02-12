package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.settings.Category;
import me.stahu.gsblockshuffle.settings.CategoryTree;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;

public final class GSBlockShuffle extends JavaPlugin {
    private File settingsFile;
    private File includedBlocksFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);
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
//    private void createIncludedBlocksFile() {
//        try {
//            if (!this.includedBlocksFile.exists()) {
//                this.saveResource("includedBlocks.yml", false);
//            }
//            System.out.println("Path of the created file: " + this.includedBlocksFile.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Handle the error here
//        }
//    }
}
