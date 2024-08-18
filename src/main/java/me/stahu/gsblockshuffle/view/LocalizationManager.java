package me.stahu.gsblockshuffle.view;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    private final Map<String, String> messages = new HashMap<>();

    public LocalizationManager(String languageCode) throws IOException {
        loadLanguageFile(languageCode);
    }

    private void loadLanguageFile(String languageCode) throws IOException {
        File file = new File("plugins/GSBlockShuffle/lang/messages_" + languageCode + ".yml");
        if (!file.exists()) {
            throw new IOException("Language file not found: " + languageCode);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            messages.put(key, config.getString(key));
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "Message not found: " + key);
    }
}
