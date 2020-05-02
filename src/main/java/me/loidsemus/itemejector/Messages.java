package me.loidsemus.itemejector;

import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Messages {

    private ItemEjector plugin;

    private Properties properties = new Properties();
    private Properties fallbackProps = new Properties();

    public Messages(ItemEjector plugin) {
        this.plugin = plugin;

        try {
            fallbackProps.load(plugin.getResource("lang_default.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the default language file from the plugin jar, REPLACING the one in the plugin directory.
        plugin.saveResource("lang_default.properties", true);
    }

    public void load(String languageCode) throws FileNotFoundException {
        properties.clear();

        File langFile = new File(plugin.getDataFolder(), "lang_" + languageCode + ".properties");
        FileInputStream inputStream = new FileInputStream(langFile);

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key, boolean prefix) {
        StringBuilder builder = new StringBuilder();

        if(prefix && !isNullOrEmpty(properties.getProperty("prefix"))) {
            builder.append(ChatColor.translateAlternateColorCodes('&', properties.getProperty("prefix"))).append(" ");
        }

        if(!isNullOrEmpty(properties.getProperty(key))) {
            builder.append(ChatColor.translateAlternateColorCodes('&', properties.getProperty(key)));
        } else {
            builder.append(ChatColor.translateAlternateColorCodes('&', fallbackProps.getProperty(key)));
        }

        return builder.toString();
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
