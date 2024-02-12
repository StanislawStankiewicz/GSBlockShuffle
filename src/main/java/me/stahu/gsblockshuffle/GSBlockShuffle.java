package me.stahu.gsblockshuffle;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GSBlockShuffle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Loader.parseYaml("block_list_categorized.yml", this, categoryTree); // parse the block_list_categorized.yml file and store the result in the categoryTree object

        Loader.dumpYaml("plugins\\GSBlockShuffle\\block_list_categorized.yml", this, categoryTree); // dump the categoryTree object to the block_list_categorized.yml file
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
                this.saveResource("includedBlocks.yml", false);
            }
            System.out.println("Path of the created file: " + this.includedBlocksFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error here
        }
    }
}
