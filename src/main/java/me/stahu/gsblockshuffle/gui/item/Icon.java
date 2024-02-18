package me.stahu.gsblockshuffle.gui.item;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Passive Icon for GUI
 */
public class Icon extends GuiItemSlot {

    /**
     * Icon constructor
     *
     * @param displayItem The display item
     */
    public Icon(ItemStack displayItem) {
        super();
        this.itemStack = displayItem;
    }

    @Override
    public void slotAction(HumanEntity ent) {
        // do nothing
    }
}
