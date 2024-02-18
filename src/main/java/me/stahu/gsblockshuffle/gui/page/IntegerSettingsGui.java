package me.stahu.gsblockshuffle.gui.page;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.item.GuiItemSlot;
import me.stahu.gsblockshuffle.gui.GuiUtils;
import me.stahu.gsblockshuffle.gui.item.Icon;
import me.stahu.gsblockshuffle.gui.item.NavigationButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * GUI Page that allows the user to change an integer value in config
 */
public class IntegerSettingsGui extends GuiPage {
    private final GSBlockShuffle plugin;
    private final String name;
    private final YamlConfiguration settings;
    private final String intPath;
    private int integerValue;
    private final GuiItemSlot[] slotArray = new GuiItemSlot[9];

    /**
     * Constructor for the IntegerSettingsGui class
     *
     * @param name        name of the setting to be displayed in the GUI
     * @param description description of the setting to be displayed in the GUI
     * @param parentPage  the parent page of this page
     * @param settings    the settings file
     * @param intPath     the path to the integer in the settings file
     * @param plugin      the plugin that this page is part of
     */
    public IntegerSettingsGui(String name, String description, GuiPage parentPage, YamlConfiguration settings, String intPath, GSBlockShuffle plugin) {
        super(name, 1, parentPage);
        this.plugin = plugin;
        this.name = name;
        this.settings = settings;
        this.intPath = intPath;
        this.integerValue = settings.getInt(intPath);
        //register event
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //create buttons
        slotArray[0] = new Icon(GuiUtils.createGuiItem(Material.STRUCTURE_VOID, "Reset", 1));
        slotArray[1] = new Icon(GuiUtils.createGuiItem(Material.END_CRYSTAL, "Infinity", 1));
        slotArray[2] = new Icon(GuiUtils.createGuiItem(Material.RED_STAINED_GLASS_PANE, "-10", 10));
        slotArray[3] = new Icon(GuiUtils.createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, "-1", 1));
        slotArray[4] = new Icon(GuiUtils.createGuiItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, name, description));
        slotArray[5] = new Icon(GuiUtils.createGuiItem(Material.LIME_STAINED_GLASS_PANE, "+1", 1));
        slotArray[6] = new Icon(GuiUtils.createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "+10", 10));
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
                this.integerValue -= 10;
                break;
            case 3:
                this.integerValue -= 1;
                break;
            case 5:
                this.integerValue += 1;
                break;
            case 6:
                this.integerValue += 10;
                break;
            case 0:
                this.integerValue = 10;
                break;
            case 8: //back button
                this.slotArray[8].slotAction(e.getWhoClicked());
                return;
            default:
                break;
        }
        //cap the value
        if (this.integerValue < 1) {
            this.integerValue = 1;
        }
        //check for infinity button
        if (e.getRawSlot() == 1) {
            this.integerValue = -1;
        }

        //save the changes to the config
        settings.set(intPath, integerValue);
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
        //update value in lore
        ItemMeta meta = slotArray[4].itemStack.getItemMeta();
        meta.setDisplayName(name + ": " + integerValue);
        slotArray[4].itemStack.setItemMeta(meta);

        //update the items in the inventory
        for (int i = 0; i < slotArray.length; i++) {
            if (slotArray[i] != null) {
                inv.setItem(i, slotArray[i].itemStack);
            }
        }
    }
}
