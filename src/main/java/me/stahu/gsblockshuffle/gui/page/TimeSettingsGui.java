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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TimeSettingsGui extends GuiPage {
    private final String name;
    NumberDisplay minutesDisplay;
    NumberDisplay secondsDisplay;
    private final GuiItemSlot[] slotArray = new GuiItemSlot[9];
    YamlConfiguration settings;
    String timeIntPath;
    GSBlockShuffle plugin;
    int time;
    private int minValue;
    private int secondIncrement;
    private int firstIncrement;

    /**
     * Constructor for the TimeSettingsGui class
     *
     * @param name        the name of the setting
     * @param description the description of the setting
     * @param parentPage  the parent page of this page
     * @param settings    the YamlConfiguration object that contains the settings
     * @param timeIntPath the path to the time setting in the settings
     * @param plugin      the plugin
     */
    public TimeSettingsGui(String name, String description, GuiPage parentPage, YamlConfiguration settings, String timeIntPath, GSBlockShuffle plugin, int firstIncrement, int secondIncrement, int minValue) {
        super("Time Settings", 1, parentPage);

        this.plugin = plugin;
        this.name = name;
        this.settings = settings;
        this.timeIntPath = timeIntPath;
        this.time = settings.getInt(timeIntPath);
        this.minValue = minValue;
        this.secondIncrement = secondIncrement;
        this.firstIncrement = firstIncrement;

        //register event
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //create time display
        minutesDisplay = new NumberDisplay();
        secondsDisplay = new NumberDisplay();

        //create time gui items
        slotArray[0] = minutesDisplay;
        slotArray[1] = secondsDisplay;
        slotArray[2] = new Icon(GuiUtils.createGuiItem(Material.RED_STAINED_GLASS_PANE, "-60s", secondIncrement));
        slotArray[3] = new Icon(GuiUtils.createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, "-15s", firstIncrement));
        slotArray[4] = new Icon(GuiUtils.createGuiItem(Material.CLOCK, name, description));
        slotArray[5] = new Icon(GuiUtils.createGuiItem(Material.LIME_STAINED_GLASS_PANE, "+15s", firstIncrement));
        slotArray[6] = new Icon(GuiUtils.createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "+60s", secondIncrement));
        slotArray[8] = new NavigationButton(GuiUtils.createGuiItem(Material.BARRIER, "Back", "Go back to the previous page"), parentPage);

        updateItems();
    }

    @Override
    public void open(HumanEntity ent) {
        ent.openInventory(this.inv);
    }

    @Override
    public void close(HumanEntity ent) {

    }


    /**
     * Handles the event of a player clicking on an item in the GUI
     *
     * @param e the InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        //button logic
        switch (e.getRawSlot()) {
            case 2:
                this.time -= secondIncrement;
                break;
            case 3:
                this.time -= firstIncrement;
                break;
            case 5:
                this.time += firstIncrement;
                break;
            case 6:
                this.time += secondIncrement;
                break;
            case 8: //back button
                this.slotArray[8].slotAction(e.getWhoClicked());
                return;
            default:
                break;
        }
        //cap time
        if (this.time < minValue) {
            this.time = minValue;
        }

        //update time
        settings.set(timeIntPath, time);
        plugin.saveConfiguration();

        updateItems();
    }

    /**
     * Cancels the event of a player dragging an item in the GUI
     *
     * @param e the InventoryDragEvent
     */
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    public void updateItems() {
        minutesDisplay.setNumber(time / 60);
        secondsDisplay.setNumber(time % 60);

        //update time in lore
        ItemMeta meta = slotArray[4].itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name + ": " + time + "s ("+time/60+"m "+time%60+"s)");
        slotArray[4].itemStack.setItemMeta(meta);


        for (int i = 0; i < slotArray.length; i++) {
            if (slotArray[i] != null) {
                inv.setItem(i, slotArray[i].itemStack);
            }
        }
    }
}