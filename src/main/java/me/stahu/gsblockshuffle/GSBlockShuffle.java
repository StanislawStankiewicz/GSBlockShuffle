package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.commands.BlockShuffleCommand;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.event.PlayerListener;
import me.stahu.gsblockshuffle.gui.page.MainMenuGui;
import me.stahu.gsblockshuffle.settings.CategoryTree;
import me.stahu.gsblockshuffle.team.TeamsManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class GSBlockShuffle extends JavaPlugin {
    private File settingsFile;
    private YamlConfiguration settings;
    private File includedBlocksFile;
    public CategoryTree categoryTree;
    public GameStateManager gameStateManager;
    private TeamsManager teamsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        this.includedBlocksFile = this.getDataFolder().toPath().resolve("block_list_categorized.yml").toFile();
        this.createIncludedBlocksFile();

        //load categories configuration
        this.categoryTree = new CategoryTree();
        try {
            categoryTree.parseYaml(includedBlocksFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create gui
        MainMenuGui mainMenuGui = new MainMenuGui(null, settings,this);

        // TODO setting this up immediately is inefficient
        this.teamsManager = new TeamsManager(settings, this);
        gameStateManager= new GameStateManager(settings, this, teamsManager);

        //register events for PlayerListener
        getServer().getPluginManager().registerEvents(new PlayerListener(settings, this, gameStateManager, teamsManager), this);


        BlockShuffleCommand blockShuffleCommand = new BlockShuffleCommand(mainMenuGui, gameStateManager, settings, this, teamsManager);
        //register commands
        this.getCommand("gsblockshuffle").setExecutor(blockShuffleCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        teamsManager.clearScoreboards();
        gameStateManager.clearBossBars();
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
        this.categoryTree.saveConfiguration(includedBlocksFile.getPath());
        //save settings
        try {
            this.settings.save(this.settingsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void sendMessage(Player player, String message) {
        String prefix = "[" + ChatColor.BLUE + "BlockShuffle" + ChatColor.WHITE + "] ";

        player.sendMessage(prefix + message);
    }
}
