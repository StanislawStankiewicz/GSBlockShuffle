package me.stahu.gsblockshuffle.gui.item;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import me.stahu.gsblockshuffle.gui.GuiUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PresetButton extends GuiItemSlot{
    String presetName;
    GSBlockShuffle plugin;
    public PresetButton(String presetName, GSBlockShuffle plugin) {
        super();
        this.presetName = presetName;
        this.plugin = plugin;
        itemStack = GuiUtils.createGuiItem(Material.FILLED_MAP, presetName, "Load preset " + presetName + " into settings");
    }

    @Override
    public void slotAction(HumanEntity ent) {
        plugin.setPreset(presetName);

        plugin.sendMessage((Player) ent, "Preset changed to: " + presetName);
    }
}
