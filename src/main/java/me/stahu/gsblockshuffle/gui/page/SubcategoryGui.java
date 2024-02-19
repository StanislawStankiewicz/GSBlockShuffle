package me.stahu.gsblockshuffle.gui.page;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.GuiUtils;
import me.stahu.gsblockshuffle.gui.item.*;
import me.stahu.gsblockshuffle.settings.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Class that represents a page in the GUI that displays categories and subcategories
 */
public class SubcategoryGui extends GuiPage implements Listener {
    private final GuiItemSlot[] slotArray = new GuiItemSlot[54];
    private final GSBlockShuffle plugin;

    /**
     * Constructor for the SubcategoryGui class
     *
     * @param screenName    the name of the current GUI screen
     * @param parentPage    the parent page of this page
     * @param subcategories the subcategories of the category
     * @param plugin        the plugin that this page is part of
     */
    public SubcategoryGui(String screenName, GuiPage parentPage, Category[] subcategories, GSBlockShuffle plugin) {
        super(screenName, 6, parentPage);
        this.plugin = plugin;
        //register event
        Bukkit.getPluginManager().registerEvents(this, plugin);

        //fill the slot array with the subcategories
        if (subcategories == null || subcategories.length == 0) {
            return;
        } else {
            for (int i = 0; i < subcategories.length; i++) {
                if ((0 <= i) && (i <= 8)) {
                    placeCategoryHeader(i, 0, subcategories);
                } else if ((9 <= i) && (i <= 17)) {
                    placeCategoryHeader(i, 9, subcategories);
                } else if ((18 <= i) && (i <= 26)) {
                    placeCategoryHeader(i, 18, subcategories);
                }
            }
        }

        //create back button
        this.slotArray[53] = new NavigationButton(GuiUtils.createGuiItem(Material.BARRIER, "Back", "Go back to the previous page"), parentPage);


        updateItems();
    }

    /**
     * Method to place a category header in the GUI
     *
     * @param index         index of slotArray
     * @param offset        index offset
     * @param subcategories the subcategories of the category
     */
    private void placeCategoryHeader(int index, int offset, Category[] subcategories) {

        //find the first item in the subcategory
        Category tempCategory = subcategories[index];
        while (tempCategory.subCategories != null) {
            tempCategory = tempCategory.subCategories.get(0);
        }

        //check if the first item is an optainable item if not use FILLED_MAP
        Material categoryMaterial = Material.getMaterial(tempCategory.elements.get(0).get(0));
        if ((categoryMaterial.isItem() == false) || categoryMaterial.isAir()) {
            categoryMaterial = Material.getMaterial("FILLED_MAP");
        }

        //create the category header item
        String firstLoreLine = ChatColor.AQUA + (subcategories[index].subCategories == null ? "There are no subcategories" : "Click to see " + subcategories[index].subCategories.size() + " more subcategor" + (subcategories[index].subCategories.size() == 1 ? "y" : "ies"));
        ArrayList<ArrayList<String>> allBlocks = subcategories[index].getBlocks();
        String[] lore;
        if (subcategories[index].elements != null) {
            lore = new String[allBlocks.size() + 1];
            lore[0] = firstLoreLine;
            for (int i = 0; i < allBlocks.size(); i++) {
                lore[i + 1] = allBlocks.get(i).get(0);
            }
        }else {
            lore = new String[1];
            lore[0] = firstLoreLine;
        }

        ItemStack categoryItem = GuiUtils.createGuiItem(
                categoryMaterial,
                (ChatColor.RESET + subcategories[index].name.substring(0, 1).toUpperCase() + subcategories[index].name.substring(1).toLowerCase()).replace("_", " "),lore);

        // do not create subcategory gui if there are no subcategories
        if (subcategories[index].subCategories == null || subcategories[index].subCategories.isEmpty())
            slotArray[index + offset] = new CategoryHeader(
                    categoryItem,
                    subcategories[index],
                    null
            );
        else
            slotArray[index + offset] = new CategoryHeader(
                    categoryItem,
                    subcategories[index],
                    new SubcategoryGui(subcategories[index].name,
                            this,
                            subcategories[index].subCategories.toArray(new Category[0]), plugin
                    ));

        // add the inclusion switch below the category header
        slotArray[index + offset + 9] = new CategoryInclusionSwitch(subcategories[index], subcategories[index].isIncluded ? 1 : 0);
    }

    /**
     * Refreshes the items in the GUI to reflect the current state of slotArray
     */
    public void updateItems() {
        for (int i = 0; i < 54; i++) {
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

        slotArray[e.getRawSlot()].slotAction(e.getWhoClicked());

        //save the changes to the config
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


}
