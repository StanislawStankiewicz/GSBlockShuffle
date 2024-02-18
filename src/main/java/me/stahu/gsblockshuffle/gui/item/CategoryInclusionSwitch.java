package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.gui.GuiUtils;
import me.stahu.gsblockshuffle.settings.Category;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents a switch for including or excluding a category in the shuffle.
 */
public class CategoryInclusionSwitch extends GuiItemSlot {
    private final Category category;
    ItemStack[] switchArray;
    int switchIndex;

    /**
     * This method should be called when the slot is clicked.
     * It cycles through the switch states and updates the category inclusion accordingly.
     *
     * @param ent The HumanEntity that clicked the slot.
     */
    @Override
    public void slotAction(HumanEntity ent) {
        switchIndex = (switchIndex + 1) % switchArray.length;
        itemStack = switchArray[switchIndex];
        this.category.setIncluded(switchIndex == 1);
    }

    /**
     * The constructor for the CategoryInclusionSwitch class.
     * It initializes the category, switchArray, switchIndex, and itemStack.
     *
     * @param category    The category to be included or excluded.
     * @param switchIndex The initial index of the switch state.
     */
    public CategoryInclusionSwitch(Category category, int switchIndex) {
        super();
        this.category = category;
        this.switchArray = new ItemStack[]{GuiUtils.createGuiItem(Material.RED_STAINED_GLASS_PANE, "Include", "Include this category in the shuffle"),
                GuiUtils.createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "Exclude", "Exclude this category from the shuffle")};
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
