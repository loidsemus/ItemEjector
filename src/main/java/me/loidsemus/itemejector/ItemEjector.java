package me.loidsemus.itemejector;

import co.aikar.commands.BukkitCommandManager;
import me.loidsemus.itemejector.commands.MainCommand;
import me.loidsemus.itemejector.config.Config;
import me.loidsemus.itemejector.database.DataSource;
import me.loidsemus.itemejector.database.SQLiteDataSource;
import me.loidsemus.itemejector.listeners.PlayerItemActionListener;
import me.loidsemus.itemejector.listeners.PlayerJoinLeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;

public class ItemEjector extends JavaPlugin {

    private Config customConfig;
    private Messages messages;
    private DataSource dataSource;
    private PlayerManager playerManager;

    private String newVersion;

    @Override
    public void onEnable() {
        customConfig = new Config(this, new File(getDataFolder(), "config.yml"));
        messages = new Messages(this);
        loadConfigAndMessages();

        dataSource = new SQLiteDataSource(this);
        playerManager = new PlayerManager(this);

        registerEvents();
        registerCommands();

        // Load all players if server is not empty, to support /reload
        if (getServer().getOnlinePlayers().size() > 0) {
            playerManager.loadAllPlayers();
        }

        int pluginId = 75548;

        // bStats
        MetricsLite metrics = new MetricsLite(this, pluginId);

        // Check for updates
        new UpdateChecker(this, pluginId).getLatestVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().log(Level.INFO, "There is a new version available on SpigotMC: " + version);
                newVersion = version;
            }
        });
    }

    public void loadConfigAndMessages() {
        getLogger().log(Level.INFO, "(Re)loading configs and messages");
        customConfig.load();
        String languageCode = getCustomConfig().getConfig().getString("lang");

        try {
            messages.load(languageCode);
        } catch (FileNotFoundException e) {
            getLogger().log(Level.SEVERE, "No language file matches the code \"" + languageCode + "\"!" +
                    " Please make one by copying the contents of lang_default.properties, or changing the config value to \"default\"." +
                    " DO NOT change the values in lang_default.properties!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        getLogger().log(Level.INFO, "Loaded configs and messages (" + languageCode + ")");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerItemActionListener(this), this);
    }

    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(new MainCommand(this));
    }

    @Override
    public void onDisable() {
        playerManager.saveAllPlayers();
    }

    public Config getCustomConfig() {
        return customConfig;
    }

    public Messages getMessages() {
        return messages;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public String getNewVersion() {
        return newVersion;
    }
}
