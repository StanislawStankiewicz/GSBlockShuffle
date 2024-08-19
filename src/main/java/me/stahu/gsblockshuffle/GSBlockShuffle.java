package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.api.BukkitAPI;
import me.stahu.gsblockshuffle.api.ServerAPI;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.controller.SoundController;
import me.stahu.gsblockshuffle.event.BlockAssignEvent;
import me.stahu.gsblockshuffle.event.GameEventDispatcher;
import me.stahu.gsblockshuffle.event.handler.*;
import me.stahu.gsblockshuffle.event.listener.*;
import me.stahu.gsblockshuffle.event.type.*;
import me.stahu.gsblockshuffle.game.assigner.BlockAssignerFactory;
import me.stahu.gsblockshuffle.game.blocks.BlockSelector;
import me.stahu.gsblockshuffle.game.difficulty.DifficultyIncrementer;
import me.stahu.gsblockshuffle.game.eliminator.TeamEliminator;
import me.stahu.gsblockshuffle.game.score.PointsAwarder;
import me.stahu.gsblockshuffle.manager.GameManager;
import me.stahu.gsblockshuffle.manager.GameManagerImpl;
import me.stahu.gsblockshuffle.manager.PlayersManager;
import me.stahu.gsblockshuffle.manager.PlayersManagerImpl;
import me.stahu.gsblockshuffle.model.CategoryTree;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.cli.command.BlockShuffleCommands;
import me.stahu.gsblockshuffle.view.sound.SoundPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GSBlockShuffle extends JavaPlugin {

    private Config config;
    private GameController gameController;
    private MessageController messageController;
    private PlayersManager playersManager;
    private SoundPlayer soundPlayer;
    private ServerAPI serverAPI;
    private LocalizationManager localizationManager;

    private Set<Player> players;
    private final Set<Team> teams = new HashSet<>();

    private static final String BLOCKS_FILE_PATH = "block_list_categorized.yml";
    private static final String SETTINGS_FILE_PATH = "settings.yml";
    private static final String PRESETS_FOLDER = "presets";
    private static final String LANG_FOLDER = "lang";

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfiguration();
        initializeAPIs();
        initializeManagers();
        initializeLocalization();
        initializeSounds();
        initializeGameController();
        registerEventListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfiguration() {
        // create and load settings
        File settingsFile = new File(getDataFolder(), SETTINGS_FILE_PATH);
        if (!settingsFile.exists()) {
            saveResource(SETTINGS_FILE_PATH, false);
        }
        config = new Config(settingsFile);
        // create and load included blocks
        File blocksFile = new File(getDataFolder(), BLOCKS_FILE_PATH);
        if (!blocksFile.exists()) {
            saveResource(BLOCKS_FILE_PATH, false);
        }
        // create presets
        for (String resource : getResourcesInFolder(PRESETS_FOLDER)) {
            saveResource(resource, false);
        }
        // create lang files
        for (String resource : getResourcesInFolder(LANG_FOLDER)) {
            saveResource(resource, false);
        }
    }

    private void initializeAPIs() {
        serverAPI = new BukkitAPI();
    }

    private void initializeManagers() {
        players = serverAPI.getPlayers();
        playersManager = new PlayersManagerImpl(teams, players);
        messageController = new MessageController(players);
    }

    private void initializeLocalization() {
        try {
            localizationManager = new LocalizationManager("en");
        } catch (IOException e) {
            // TODO: handle this exception
            throw new RuntimeException(e);
        }
    }

    private void initializeSounds() {
        SoundController soundController = new SoundController(this, Bukkit.getScheduler(), players);
        soundPlayer = new SoundPlayer(soundController, !config.isMuteSound());
    }

    private void initializeGameController() {
        MessageBuilder messageBuilder = new MessageBuilder(localizationManager);
        GameEventDispatcher dispatcher = createEventDispatcher(messageBuilder);
        PointsAwarder pointsAwarder = new PointsAwarder(config);

        CategoryTree categoryTree;
        try {
            categoryTree = CategoryTree.parseYaml(
                    this.getDataFolder().toPath().resolve(BLOCKS_FILE_PATH).toString()
            );
        } catch (FileNotFoundException e) {
            // TODO: handle this exception
            throw new RuntimeException(e);
        }

        GameManager gameManager = GameManagerImpl.builder()
                .blockSelector(new BlockSelector(config, categoryTree))
                .dispatcher(dispatcher)
                .playersManager(playersManager)
                .blockAssigner(BlockAssignerFactory.getBlockAssigner(config, dispatcher))
                .teamEliminator(new TeamEliminator(config))
                .difficultyIncrementer(new DifficultyIncrementer(config))
                .teams(teams)
                .config(config)
                .build();
        gameController = new GameController(this, config, dispatcher, gameManager, pointsAwarder);
    }

    private GameEventDispatcher createEventDispatcher(MessageBuilder messageBuilder) {
        return new GameEventDispatcher()
                .registerListener(InvokeGameStartEvent.class, new InvokeGameStartListener(new InvokeGameStartHandler(soundPlayer)))
                .registerListener(GameStartEvent.class, new GameStartListener(new GameStartHandler(messageController, soundPlayer, messageBuilder)))
                .registerListener(BlockAssignEvent.class, new BlockAssignListener(new BlockAssignHandler(messageController, messageBuilder)))
                .registerListener(BlockFoundEvent.class, new BlockFoundListener(new BlockFoundHandler(messageController, soundPlayer, messageBuilder)))
                .registerListener(BreakStartEvent.class, new BreakStartListener(new BreakStartHandler(soundPlayer)))
                .registerListener(GameEndEvent.class, new GameEndListener(new GameEndHandler(messageController, soundPlayer, messageBuilder)));
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(gameController, playersManager), this);
    }

    private void registerCommands() {
        BlockShuffleCommands commands = new BlockShuffleCommands(gameController);
        Objects.requireNonNull(this.getCommand("bs")).setExecutor(commands);
    }

    private List<String> getResourcesInFolder(String resourcePath) {
        List<String> filenames = new ArrayList<>();

        try {
            CodeSource src = GSBlockShuffle.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                    ZipEntry ze;
                    while ((ze = zip.getNextEntry()) != null) {
                        String entryName = ze.getName();
                        if (entryName.startsWith(resourcePath + "/") && !ze.isDirectory()) {
                            filenames.add(entryName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            String msg = String.format("Error getting resources in folder: %s", resourcePath);
            getLogger().log(Level.WARNING, msg, e);
        }

        return filenames;
    }
}
