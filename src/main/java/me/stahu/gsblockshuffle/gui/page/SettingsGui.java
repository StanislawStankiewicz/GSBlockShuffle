package me.stahu.gsblockshuffle.gui.page;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.GuiUtils;
import me.stahu.gsblockshuffle.gui.item.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

/**
 * GUI page that displays settings
 */
public class SettingsGui extends GuiPage {
    private final GuiItemSlot[] slotArray = new GuiItemSlot[36];
    GuiItemSlot settingsIcon;
    private final YamlConfiguration settings;
    private final GSBlockShuffle plugin;

    /**
     * Constructor for the SettingsGui class
     *
     * @param parentPage the parent page of this page
     * @param settings   the YamConfiguration object that stores the settings
     * @param plugin     plugin
     */
    public SettingsGui(GuiPage parentPage, YamlConfiguration settings, GSBlockShuffle plugin) {
        super("Settings", 4, parentPage);
        this.plugin = plugin;
        this.settings = settings;
        //register event
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //time settings
        this.slotArray[0] = new NavigationButton(GuiUtils.createGuiItem(Material.CLOCK, "Round Time", "Set the time for each round"), new TimeSettingsGui("Round Time", "Change round duration", this, settings, "roundTime", plugin, 15, 60, 15));
        this.slotArray[9] = new NavigationButton(GuiUtils.createGuiItem(Material.CLOCK, "Time Between Rounds", "Set the time between rounds"), new TimeSettingsGui("Break Time", "Change time between rounds", this, settings, "roundBreakTime", plugin, 5, 30, 0));

        //rounds settings
        this.slotArray[1] = new NavigationButton(GuiUtils.createGuiItem(Material.BELL, "Rounds", "Set the amount of rounds"), new IntegerSettingsGui("Rounds", "Change the amount of rounds", this, settings, "roundsPerGame", plugin));

        //difficulty settings
        ItemStack[] switchArray = new ItemStack[10];
        for (int i = 0; i < 10; i++) {
            switchArray[i] = GuiUtils.createGuiItem(Material.SPAWNER, "Difficulty: " + i, i == 0 ? 1 : i, "Change Current difficulty");
        }
        this.slotArray[18] = new ItemSwitch(switchArray, settings.getInt("difficulty"));

        //create boolean switches
        createSettingsSwitch(settings, "includeLowerDifficulties", 2);
        createSettingsSwitch(settings, "includeVariants", 3);
        createSettingsSwitch(settings, "treatAllAsIndividualBlocks", 4);
        createSettingsSwitch(settings, "allPlayersRequiredForTeamWin", 5);
        createSettingsSwitch(settings, "eliminateAfterRound", 6);
        createSettingsSwitch(settings, "firstToWin", 11);
        createSettingsSwitch(settings, "teamScoreIncrementPerPlayer", 12);
        createSettingsSwitch(settings, "muteSounds", 13);
        createSettingsSwitch(settings, "showTeamCoords", 14);
        createSettingsSwitch(settings, "displaySplashWinnerMessage", 15);
        createSettingsSwitch(settings, "showTeamCompass", 20);
        createSettingsSwitch(settings, "disablePvP", 21);

        // block assignment mode switch
        ItemStack[] blockAssignmentModeSwitchArray = new ItemStack[3];
        blockAssignmentModeSwitchArray[0] = GuiUtils.createGuiItem(Material.FROGSPAWN, "Block Assignment Mode:", ChatColor.AQUA + "onePerPlayer");
        blockAssignmentModeSwitchArray[1] = GuiUtils.createGuiItem(Material.BEETROOT_SEEDS, "Block Assignment Mode:", ChatColor.AQUA + "onePerTeam");
        blockAssignmentModeSwitchArray[2] = GuiUtils.createGuiItem(Material.OAK_BUTTON, "Block Assignment Mode:", ChatColor.AQUA + "onePerRound");
        this.slotArray[27] = new ItemSwitch(blockAssignmentModeSwitchArray, getIndexOfBlockAssignmentMode(settings.getString("blockAssignmentMode")));

        //teleport settings
        ItemStack[] teleportModeSwitchArray = new ItemStack[4];
        teleportModeSwitchArray[0] = GuiUtils.createGuiItem(Material.ENDER_PEARL, "Allowed Teleports Mode:", ChatColor.AQUA + "disabled");
        teleportModeSwitchArray[1] = GuiUtils.createGuiItem(Material.ENDER_PEARL, "Allowed Teleports Mode:", ChatColor.AQUA + "amountPerTeam");
        teleportModeSwitchArray[2] = GuiUtils.createGuiItem(Material.ENDER_PEARL, "Allowed Teleports Mode:", ChatColor.AQUA + "amountPerPlayer");
        teleportModeSwitchArray[3] = GuiUtils.createGuiItem(Material.ENDER_PEARL, "Allowed Teleports Mode:", ChatColor.AQUA + "unlimited");
        this.slotArray[28] = new ItemSwitch(teleportModeSwitchArray, getIndexOfTeleportMode(settings.getString("teleportMode")));
        this.slotArray[29] = new NavigationButton(GuiUtils.createGuiItem(Material.ENDER_EYE, "Amount Of Teleports", "Change the number of allowed teleports"),new IntegerSettingsGui("Teleport Amount Per Round", "Change the amount of teleports", this, settings, "amountOfTeleportsPerRound", plugin));

        //icon that has the config file in lore
        this.settingsIcon = new Icon(GuiUtils.createGuiItem(Material.FILLED_MAP, "Config File", ""));
        this.slotArray[8] = settingsIcon;

        //create back button
        this.slotArray[35] = new NavigationButton(GuiUtils.createGuiItem(Material.BARRIER, "Back", "Go back to the previous page"), parentPage);

        updateItems();
    }

    /**
     * Method to create a switch for a boolean setting
     *
     * @param settings the YamlConfiguration object that stores the settings
     * @param name     the name of the setting
     * @param index    the index of the slotArray
     */
    public void createSettingsSwitch(YamlConfiguration settings, String name, int index) {
        this.slotArray[index] = new SettingsSwitch(settings, name);
    }

    @Override
    public void open(HumanEntity ent) {
        ent.openInventory(this.inv);
    }

    @Override
    public void close(HumanEntity ent) {

    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        //call the slotAction method of the clicked item
        slotArray[e.getRawSlot()].slotAction(e.getWhoClicked());

        //save the changes to the config
        settings.set("difficulty", ((ItemSwitch) slotArray[18]).getState());
        settings.set("blockAssignmentMode", getBlockAssignmentMode(((ItemSwitch) slotArray[27]).getState()));
        settings.set("teleportMode", getTeleportMode(((ItemSwitch) slotArray[28]).getState()));
        plugin.saveConfiguration();

        updateItems();
    }

    public void updateItems() {
        for (int i = 0; i < slotArray.length; i++) {
            if (slotArray[i] != null) {
                inv.setItem(i, slotArray[i].itemStack);
            }
        }

        //update the lore of the settingsIcon
        ArrayList<String> settingsLines = new ArrayList<>();
        for (String key : settings.getKeys(true)) {
            settingsLines.add(ChatColor.DARK_AQUA + key + ": " +ChatColor.YELLOW+ settings.get(key));
        }
        ItemMeta tempMeta = settingsIcon.itemStack.getItemMeta();
        tempMeta.setLore(settingsLines);
        settingsIcon.itemStack.setItemMeta(tempMeta);
    }

    /**
     * Method to get the index of the block assignment mode from string
     *
     * @param value String value of the block assignment mode
     * @return the index of the block assignment mode
     */
    private int getIndexOfBlockAssignmentMode(String value) {
        if (Objects.equals(value, "onePerPlayer")) {
            return 0;
        } else if (Objects.equals(value, "onePerTeam")) {
            return 1;
        } else if (Objects.equals(value, "onePerRound")) {
            return 2;
        }
        return -1;
    }

    /**
     * Method to get the block assignment mode String from its index
     *
     * @param index the index of the block assignment mode
     * @return the block assignment mode
     */
    private String getBlockAssignmentMode(int index) {
        if (index == 0) {
            return "onePerPlayer";
        } else if (index == 1) {
            return "onePerTeam";
        } else if (index == 2) {
            return "onePerRound";
        }
        return "onePerPlayer";
    }

    private int getIndexOfTeleportMode(String value) {
        if (Objects.equals(value, "disabled")) {
            return 0;
        } else if (Objects.equals(value, "amountPerTeam")) {
            return 1;
        } else if (Objects.equals(value, "amountPerPlayer")) {
            return 2;
        } else if (Objects.equals(value, "unlimited")) {
            return 3;
        }
        return -1;
    }

    private String getTeleportMode(int index) {
        if (index == 0) {
            return "disabled";
        } else if (index == 1) {
            return "amountPerTeam";
        } else if (index == 2) {
            return "amountPerPlayer";
        } else if (index == 3) {
            return "unlimited";
        }
        return "disabled";
    }
}
