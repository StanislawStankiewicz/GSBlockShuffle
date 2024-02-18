package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.gui.GuiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Class representing a boolean switch in the GUI for toggling a config setting.
 */
public class SettingsSwitch extends GuiItemSlot{
    private String name;
    private YamlConfiguration settings;
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
        ent.sendMessage("Set " + name + " to " + switchIndex);
        itemStack = switchArray[switchIndex];
        System.out.println("Set " + name + " to " + switchIndex);
        System.out.println("Material: " + itemStack.getType());
        this.settings.set(name,switchIndex == 1);
    }

    /**
     * This method creates a new SettingsSwitch object.
     * It sets the switch to the current state of the setting.
     *
     * @param settings The settings file.
     * @param name The name of the setting.
     */
    public SettingsSwitch(YamlConfiguration settings, String name) {
        super();
        this.name = name;
        this.settings = settings;

        this.switchArray = new ItemStack[]{GuiUtils.createGuiItem(Material.RED_STAINED_GLASS_PANE,  name, ChatColor.RED+"False"),
                GuiUtils.createGuiItem(Material.GREEN_STAINED_GLASS_PANE, name,ChatColor.GREEN+"True")};
        this.switchIndex = settings.getBoolean(name) ? 1 : 0;
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
