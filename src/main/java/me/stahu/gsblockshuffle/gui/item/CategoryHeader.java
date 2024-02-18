package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.gui.page.GuiPage;
import me.stahu.gsblockshuffle.settings.Category;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * The CategoryHeader class represents a category header in a GUI.
 * Calling slotAction opens the subcategory GUI.
 */
public class CategoryHeader extends GuiItemSlot {
    Category category;
    GuiPage subCategoryPage;

    @Override
    public void slotAction(final HumanEntity ent) {
        // open subcategory gui
        if (subCategoryPage != null) {
            subCategoryPage.open(ent);
        }
    }

    /**
     * The constructor for the CategoryHeader class.
     *
     * @param displayItem    The item to display in the slot.
     * @param category       The category to be displayed.
     * @param subCategoryGui The subcategory GUI to open when the slot is clicked.
     */
    public CategoryHeader(ItemStack displayItem, Category category, GuiPage subCategoryGui) {
        super();
        this.itemStack = displayItem;
        this.category = category;
        this.subCategoryPage = subCategoryGui;
    }


}
