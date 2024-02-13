package me.stahu.gsblockshuffle.gui.page;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

/**
 * The GuiPage class is an abstract class that represents a page in a GUI.
 * It can be opened and closed.
 * It contains an Inventory.
 */
public abstract class GuiPage implements Listener {
    GuiPage parentPage;
    protected Inventory inv;
    protected String title;
    protected int rows;

    public GuiPage(String title, int rows) {
        this.rows = rows;
        this.title = title;
        parentPage = null;
        this.inv = Bukkit.createInventory(null, this.rows * 9, this.title);
    }

    public GuiPage(String title, int rows, GuiPage parentPage) {
        this.rows = rows;
        this.title = title;
        this.parentPage = parentPage;
        this.inv = Bukkit.createInventory(null, this.rows * 9, this.title);
    }

    public abstract void open(final HumanEntity ent);

    public abstract void close(final HumanEntity ent);
}
