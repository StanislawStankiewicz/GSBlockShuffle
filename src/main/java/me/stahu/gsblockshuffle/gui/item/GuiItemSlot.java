package me.stahu.gsblockshuffle.gui.item;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * The GuiItemSlot class is an abstract class that represents an actionable slot in a GUI.
 */
public abstract class GuiItemSlot {
    public ItemStack itemStack;

    /**
     * This method should be called when the slot is clicked.
     * It opens the subcategory GUI.
     *
     * @param ent The HumanEntity that clicked the slot.
     */
    public abstract void slotAction(final HumanEntity ent);
}
