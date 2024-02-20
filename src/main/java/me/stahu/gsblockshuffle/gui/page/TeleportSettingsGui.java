package me.stahu.gsblockshuffle.gui.page;

import me.stahu.gsblockshuffle.GSBlockShuffle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;

//TODO implement this class
public class TeleportSettingsGui extends GuiPage {
    public TeleportSettingsGui(String teleportSettings, String changeTeleportSettings, SettingsGui settingsGui, YamlConfiguration settings, GSBlockShuffle plugin) {
        super(teleportSettings, 1, settingsGui);

    }

    @Override
    public void open(HumanEntity ent) {

    }

    @Override
    public void close(HumanEntity ent) {

    }
}
