package me.stahu.gsblockshuffle;

import me.stahu.gsblockshuffle.api.BukkitAPI;
import me.stahu.gsblockshuffle.api.ServerAPI;
import me.stahu.gsblockshuffle.config.Config;
import me.stahu.gsblockshuffle.controller.GameController;
import me.stahu.gsblockshuffle.controller.MessageController;
import me.stahu.gsblockshuffle.controller.SoundController;
import me.stahu.gsblockshuffle.event.BlockShuffleEventDispatcher;
import me.stahu.gsblockshuffle.event.handler.game.*;
import me.stahu.gsblockshuffle.event.handler.team.*;
import me.stahu.gsblockshuffle.event.listener.PlayerListener;
import me.stahu.gsblockshuffle.event.listener.game.*;
import me.stahu.gsblockshuffle.event.listener.team.*;
import me.stahu.gsblockshuffle.event.type.game.*;
import me.stahu.gsblockshuffle.event.type.team.*;
import me.stahu.gsblockshuffle.game.assigner.BlockAssignerFactory;
import me.stahu.gsblockshuffle.game.blocks.BlockSelector;
import me.stahu.gsblockshuffle.game.difficulty.DifficultyIncrementer;
import me.stahu.gsblockshuffle.game.eliminator.TeamEliminator;
import me.stahu.gsblockshuffle.game.score.PointsAwarder;
import me.stahu.gsblockshuffle.manager.*;
import me.stahu.gsblockshuffle.model.CategoryTree;
import me.stahu.gsblockshuffle.model.Player;
import me.stahu.gsblockshuffle.model.Team;
import me.stahu.gsblockshuffle.view.LocalizationManager;
import me.stahu.gsblockshuffle.view.cli.MessageBuilder;
import me.stahu.gsblockshuffle.view.cli.command.BlockShuffleCommands;
import me.stahu.gsblockshuffle.view.cli.message.GameMessageBuilder;
import me.stahu.gsblockshuffle.view.cli.message.TeamMessageBuilder;
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
    private TeamsManager teamsManager;
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
        playersManager = new PlayersManager(players);
        teamsManager = new TeamsManagerImpl(playersManager, teams);
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
        GameMessageBuilder gameMessageBuilder = new GameMessageBuilder(localizationManager);
        TeamMessageBuilder teamMessageBuilder = new TeamMessageBuilder(localizationManager);
        MessageBuilder messageBuilder = new MessageBuilder(gameMessageBuilder, teamMessageBuilder);
        BlockShuffleEventDispatcher dispatcher = createEventDispatcher(messageBuilder);
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
                .teamsManager(teamsManager)
                .blockAssigner(BlockAssignerFactory.getBlockAssigner(config, dispatcher))
                .teamEliminator(new TeamEliminator(config))
                .difficultyIncrementer(new DifficultyIncrementer(config))
                .teams(teams)
                .config(config)
                .build();
        gameController = new GameController(this, config, dispatcher, gameManager, pointsAwarder);
    }

    private BlockShuffleEventDispatcher createEventDispatcher(MessageBuilder messageBuilder) {
        return new BlockShuffleEventDispatcher()
                .registerListener(InvokeGameStartEvent.class, new InvokeBlockShuffleStartListener(new InvokeGameStartHandler(soundPlayer)))
                .registerListener(GameStartEvent.class, new BlockShuffleStartListener(new GameStartHandler(messageController, soundPlayer, messageBuilder)))
                .registerListener(BlockAssignEvent.class, new BlockAssignListener(new BlockAssignHandler(messageController, messageBuilder)))
                .registerListener(BlockFoundEvent.class, new BlockFoundListener(new BlockFoundHandler(messageController, soundPlayer, messageBuilder)))
                .registerListener(BreakStartEvent.class, new BreakStartListener(new BreakStartHandler(soundPlayer)))
                .registerListener(GameEndEvent.class, new BlockShuffleEndListener(new GameEndHandler(messageController, soundPlayer, messageBuilder)))
                // Register team events
                .registerListener(AcceptInviteEvent.class, new AcceptInviteListener(new AcceptInviteHandler(messageController, messageBuilder)))
                .registerListener(AcceptRequestEvent.class, new AcceptRequestListener(new AcceptRequestHandler(messageController, messageBuilder)))
                .registerListener(AddPlayerToTeamEvent.class, new AddPlayerToTeamListener(new AddPlayerToTeamHandler(messageController, messageBuilder)))
                .registerListener(CreateTeamEvent.class, new CreateTeamListener(new CreateTeamHandler(messageController, messageBuilder)))
                .registerListener(InvitePlayerToTeamEvent.class, new InvitePlayerToTeamListener(new InvitePlayerToTeamHandler(messageController, messageBuilder)))
                .registerListener(KickFromTeamEvent.class, new KickFromTeamListener(new KickFromTeamHandler(messageController, messageBuilder)))
                .registerListener(LeaveTeamEvent.class, new LeaveTeamListener(new LeaveTeamHandler(messageController, messageBuilder)))
                .registerListener(RemoveTeamEvent.class, new RemoveTeamListener(new RemoveTeamHandler(messageController, messageBuilder)))
                .registerListener(RequestToJoinTeamEvent.class, new RequestToJoinTeamListener(new RequestToJoinTeamHandler(messageController, messageBuilder)))
                .registerListener(TeamFailEvent.class, new TeamFailListener(new TeamFailHandler(messageController, messageBuilder)));
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
