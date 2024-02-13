package me.stahu.gsblockshuffle.gui.item;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * The ItemSwitch class represents a switchable item in a GUI.
 * It can switch between a given array of items.
 */
public class ItemSwitch extends GuiItemSlot {
    ItemStack[] switchArray;
    int switchIndex;

    /**
     * This method should be called when the slot is clicked.
     * It cycles through the switch states.
     *
     * @param ent The HumanEntity that clicked the slot.
     */
    @Override
    public void slotAction(final HumanEntity ent) {
        switchIndex = (switchIndex + 1) % switchArray.length;
        itemStack = switchArray[switchIndex];
    }

    /**
     * The constructor for the ItemSwitch class.
     *
     * @param switchArray The array of items to switch between.
     */
    public ItemSwitch(ItemStack[] switchArray) {
        super();
        this.switchArray = switchArray;
        this.switchIndex = 0;
        this.itemStack = switchArray[switchIndex];
    }

    /**
     * The constructor for the ItemSwitch class.
     *
     * @param switchArray The array of items to switch between.
     * @param switchIndex The initial index of the switch state.
     */
    public ItemSwitch(ItemStack[] switchArray, int switchIndex) {
        super();
        this.switchArray = switchArray;
        this.switchIndex = switchIndex;
        this.itemStack = switchArray[switchIndex];
    }

    /**
     * This method returns the current state of the switch.
     *
     * @return The current state of the switch.
     */
    public int getState() {
        return switchIndex;
    }
}
