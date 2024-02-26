package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.commands.BlockShuffleCommand;
import me.stahu.gsblockshuffle.event.GameStateManager;
import me.stahu.gsblockshuffle.event.PlayerListener;
import me.stahu.gsblockshuffle.gui.BossBarTimer;
import me.stahu.gsblockshuffle.gui.TeammateCompass;
import me.stahu.gsblockshuffle.gui.page.MainMenuGui;
import me.stahu.gsblockshuffle.settings.CategoryTree;
import me.stahu.gsblockshuffle.team.TeamsManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GSBlockShuffle extends JavaPlugin {
    private File settingsFile;
    public YamlConfiguration settings;
    private File includedBlocksFile;
    public CategoryTree categoryTree;
    public GameStateManager gameStateManager;
    private TeamsManager teamsManager;
    public TeammateCompass teammateCompass;
    private BossBarTimer bossBarTimer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        this.includedBlocksFile = this.getDataFolder().toPath().resolve("block_list_categorized.yml").toFile();
        this.createIncludedBlocksFile();

        this.createPresets();

        //load categories configuration
        this.categoryTree = new CategoryTree();
        try {
            categoryTree.parseYaml(includedBlocksFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create gui
        MainMenuGui mainMenuGui = new MainMenuGui(null, settings, this);

        // TODO setting this up immediately is inefficient
        this.teamsManager = new TeamsManager(settings, this);
        this.teammateCompass = new TeammateCompass(teamsManager);
        this.bossBarTimer = new BossBarTimer(teamsManager);
        gameStateManager = new GameStateManager(settings, this, teamsManager, bossBarTimer);

        //register events for PlayerListener
        getServer().getPluginManager().registerEvents(new PlayerListener(settings, this, gameStateManager, teamsManager, teammateCompass), this);


        BlockShuffleCommand blockShuffleCommand = new BlockShuffleCommand(mainMenuGui, gameStateManager, settings, this, teamsManager);
        //register commands
        this.getCommand("blockshuffle").setExecutor(blockShuffleCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        teamsManager.clearScoreboards();
        bossBarTimer.clearBossBars();
        teammateCompass.clearCompassBars();
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setDisplayName(ChatColor.RESET + player.getName() + ChatColor.RESET);
            // reset color on tab
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
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

    public void createPresets() {
        File folder = new File(getDataFolder(), "presets");
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (String resource : getPresetFiles()) {
            System.out.println("Resource: " + resource);
            saveResource(resource, false);
        }
    }

    private List<String> getPresetFiles() {
        List<String> filenames = new ArrayList<>();

        try {
            CodeSource src = GSBlockShuffle.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze;

                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.startsWith("presets/") && !ze.isDirectory()) {
                        filenames.add(entryName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filenames;
    }

    public List<String> getPresetNames(){
        List<String> presetNames = new ArrayList<>();
        File folder = new File(getDataFolder(), "presets");
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                presetNames.add(file.getName().replace(".yml", ""));
            }
        }
        return presetNames;
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

    public void loadConfiguration() {
        this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);
        try {
            this.categoryTree.parseYaml(includedBlocksFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to change a setting in the settings.yml file.
     * Verifies that the value is valid for the setting type.
     * Note: if the setting's type was incorrectly set in the settings.yml file,
     * this method will not repair it.
     *
     * @param settings The YamlConfiguration object where the setting is to be changed.
     * @param key      The key of the setting to be changed.
     * @param value    The new value for the setting.
     * @throws IllegalArgumentException If the value is not valid for the setting type.
     */
    public void changeSetting(YamlConfiguration settings, String key, String value) throws IllegalArgumentException {
        if (settings.isBoolean(key)) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                settings.set(key, Boolean.parseBoolean(value));
            } else {
                throw new IllegalArgumentException("Value must be true or false");
            }
        } else if (settings.isInt(key)) {
            if (value.matches("\\d+")) {
                settings.set(key, Integer.parseInt(value));
            } else {
                throw new IllegalArgumentException("Value must be an integer");
            }
        } else {
            settings.set(key, value);
        }
    }

    /**
     * Used to send a message to a player as the plugin.
     * Should be used to send a message to all players.
     *
     * @param player  The player to whom the message will be sent.
     * @param message The message to be sent to the player.
     */
    public void sendMessage(Player player, String message) {
        String prefix = "[" + ChatColor.BLUE + "BlockShuffle" + ChatColor.WHITE + "] ";

        player.sendMessage(prefix + message);
    }

    public void sendMessage(Player player, TextComponent message) {
        String prefix = "[" + ChatColor.BLUE + "BlockShuffle" + ChatColor.WHITE + "] ";
        TextComponent finalMessage = new TextComponent(prefix);
        finalMessage.addExtra(message);
        player.spigot().sendMessage(finalMessage);
    }

    //check for duplicates and missing blocks
    private void testBlocks() {
        ArrayList<String> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                materials.add(material.name());
            }
        }
        ArrayList<String> ourMaterials = categoryTree.getAllBlocks();

        for (String material : ourMaterials) {
            if (!materials.contains(material)) {
                System.out.println("Extra block: " + material);
            }
        }
        for (String material : materials) {
            if (!ourMaterials.contains(material)) {
                System.out.println("Missing block: " + material);
            }
        }
        // check for doubles
        for (int i = 0; i < ourMaterials.size(); i++) {
            for (int j = i + 1; j < ourMaterials.size(); j++) {
                if (ourMaterials.get(i).equals(ourMaterials.get(j))) {
                    System.out.println("Duplicate block: " + ourMaterials.get(i));
                }
            }
        }
    }

    /**
     * Sets new settings for the game from the presets directory.
     * This method will only change the settings mentioned in the preset file.
     *
     * @param preset The name of the preset
     * @throws IllegalArgumentException If the preset file does not exist in the "presets" directory.
     */
    public void setPreset(String preset) {
        File presetFile = new File(getDataFolder(), "presets/" + preset + ".yml");
        if (!presetFile.exists()) {
            throw new IllegalArgumentException("Preset " + preset + " does not exist");
        }
        YamlConfiguration presetConfig = YamlConfiguration.loadConfiguration(presetFile);
        for (String key : presetConfig.getKeys(false)) {
            settings.set(key, presetConfig.get(key));
        }
        saveConfiguration();
    }
}
