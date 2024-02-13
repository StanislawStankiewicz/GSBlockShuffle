package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.gui.page.GuiPage;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * The NavigationButton class represents a button in a GUI that navigates to another page.
 */
public class NavigationButton extends GuiItemSlot {
    GuiPage navigateToPage;

    /**
     * This method should be called when the slot is clicked.
     * It navigates to the specified page.
     *
     * @param ent The HumanEntity that clicked the slot.
     */
    @Override
    public void slotAction(final HumanEntity ent) {
        // open subcategory gui
        if (navigateToPage != null) {
            navigateToPage.open(ent);
        } else {
            ent.closeInventory();
        }
    }

    /**
     * The constructor for the NavigationButton class.
     *
     * @param displayItem  The item to be displayed as the button.
     * @param navigateToPage The page to navigate to when slotAction is called.
     */
    public NavigationButton(ItemStack displayItem, GuiPage navigateToPage) {
        super();
        this.itemStack = displayItem;
        this.navigateToPage = navigateToPage;
    }
}
