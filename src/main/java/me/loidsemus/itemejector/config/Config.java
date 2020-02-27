package me.loidsemus.itemejector.config;

import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private final ItemEjector plugin;
    private final File configFile;

    private FileConfiguration config;

    public Config(ItemEjector plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
    }

    public void load() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
