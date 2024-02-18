package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.gui.GuiUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

/**
 * Class representing a number display in the GUI using item amount.
 */
public class NumberDisplay extends GuiItemSlot {

    @Override
    public void slotAction(HumanEntity ent) {
        //do nothing
    }

    /**
     * Constructor for the number display.
     */
    public NumberDisplay() {
        super();
        this.itemStack = GuiUtils.createGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ");
    }

    /**
     * Set the number of the display.
     * @param number The number to set.
     */
    public void setNumber(int number) {
        this.itemStack.setAmount(number);
    }
}
