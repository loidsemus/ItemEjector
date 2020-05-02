package me.loidsemus.itemejector;

import me.loidsemus.itemejector.commands.MainCommand;
import me.loidsemus.itemejector.config.Config;
import me.loidsemus.itemejector.database.DataSource;
import me.loidsemus.itemejector.database.SQLiteDataSource;
import me.loidsemus.itemejector.listeners.PlayerItemActionListener;
import me.loidsemus.itemejector.listeners.PlayerJoinLeaveListener;
import me.loidsemus.itemejector.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;

public class ItemEjector extends JavaPlugin {

    private Config customConfig;
    private Messages messages;
    private DataSource dataSource;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        customConfig = new Config(this, new File(getDataFolder(), "config.yml"));
        messages = new Messages(this);
        loadConfigAndMessages();

        dataSource = new SQLiteDataSource(this);
        playerManager = new PlayerManager(dataSource);

        registerEvents();
        registerCommands();

        // Load all players if server is not empty, to support /reload
        if (getServer().getOnlinePlayers().size() > 0) {
            getServer().getOnlinePlayers().forEach(player -> playerManager.loadPlayer(player.getUniqueId().toString()));
        }

        int pluginId = 75548;

        // bStats
        new MetricsLite(this, 7364);

        // Check for updates
        UpdateChecker updateChecker = new UpdateChecker(this, pluginId);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            updateChecker.getNewVersion(version -> getLogger().log(Level.WARNING, "There is a new version available on SpigotMC: " + version));
        }, 0L, 60L * 30L * 20L);
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
        getCommand("itemejector").setExecutor(new MainCommand(this));
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
}
