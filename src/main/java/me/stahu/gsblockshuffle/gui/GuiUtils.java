package me.stahu.gsblockshuffle.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;


/**
 * Class that contains utility methods for GUIs
 */
public class GuiUtils {

    /**
     * Creates a new item with a given material, name, and lore
     *
     * @param material the material of the item
     * @param name     the name of the item
     * @param lore     the lore of the item
     * @return the item
     */
    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Creates a new item with a given material, name, amount and lore
     *
     * @param material the material of the item
     * @param name     the name of the item
     * @param amount   the amount of the item
     * @param lore     the lore of the item
     * @return the item
     */
    public static ItemStack createGuiItem(final Material material, final String name, int amount, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }

        return item;
    }
}
