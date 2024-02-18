package me.stahu.gsblockshuffle.gui.page;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.GuiUtils;
import me.stahu.gsblockshuffle.gui.item.*;
import me.stahu.gsblockshuffle.settings.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The main menu GUI for the Block Shuffle plugin
 */
public class MainMenuGui extends GuiPage implements Listener {
    private final GuiItemSlot[] slotArray = new GuiItemSlot[9];
    private final GSBlockShuffle plugin;

    /**
     * Constructor for MainMenuGui
     *
     * @param parentPage the parent page of this page
     * @param settings   the YamConfiguration object that stores the settings of the game
     * @param plugin     the plugin
     */
    public MainMenuGui(GuiPage parentPage, YamlConfiguration settings, GSBlockShuffle plugin) {
        super("Main Menu", 1, parentPage);
        this.plugin = plugin;
        //register event
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //settings navigation button
        slotArray[0] = new NavigationButton(GuiUtils.createGuiItem(Material.COMPARATOR, "Settings", "Change the settings of the game"), new SettingsGui(this, settings, plugin));

        //category selection button
        slotArray[1] = new NavigationButton(GuiUtils.createGuiItem(Material.BOOK, "Category Selection", "Select the categories for the game"), new SubcategoryGui("Block Shuffle Settings", this, plugin.categoryTree.categories.toArray(new Category[0]), plugin));

        //start game button
        slotArray[2] = new Icon(GuiUtils.createGuiItem(Material.EMERALD_BLOCK, "Start Game", "Start the game with the current settings"));

        slotArray[3] = new Icon(GuiUtils.createGuiItem(Material.REDSTONE_BLOCK, "Stop Ghe Game", "End the current game"));
        //create back button
        this.slotArray[8] = new NavigationButton(GuiUtils.createGuiItem(Material.BARRIER, "Back", "Go back to the previous page"), parentPage);


        updateItems();
    }

    /**
     * Refreshes the items in the GUI to reflect the current state of slotArray
     */
    public void updateItems() {
        for (int i = 0; i < slotArray.length; i++) {
            if (slotArray[i] != null) {
                inv.setItem(i, slotArray[i].itemStack);
            }
        }
    }

    @Override
    public void open(final HumanEntity player) {
        player.openInventory(inv);
    }

    @Override
    public void close(final HumanEntity player) {
        player.closeInventory();
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

        // invoke the action of the clicked slot
        slotArray[e.getRawSlot()].slotAction(e.getWhoClicked());

        //start game
        if (e.getRawSlot() == 2) {
            if (!plugin.gameStateManager.setGameState(1)) {
                Player player = (Player) e.getWhoClicked();
                player.sendMessage(ChatColor.RED + "Game is already running.");
            }
        }

        //end game
        if (e.getRawSlot() == 3) {
            if (!plugin.gameStateManager.setGameState(0)) {
                Player player = (Player) e.getWhoClicked();
                player.sendMessage(ChatColor.RED + "Game is already stopped.");
            }
        }

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


}
