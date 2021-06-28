package me.loidsemus.itemejector;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import me.loidsemus.itemejector.commands.MainCommand;
import me.loidsemus.itemejector.config.GUISettings;
import me.loidsemus.itemejector.config.Settings;
import me.loidsemus.itemejector.database.DataSource;
import me.loidsemus.itemejector.database.SQLiteDataSource;
import me.loidsemus.itemejector.listeners.PlayerItemActionListener;
import me.loidsemus.itemejector.listeners.PlayerJoinLeaveListener;
import me.loidsemus.itemejector.messages.Messages;
import me.loidsemus.itemejector.utils.UpdateChecker;
import me.loidsemus.lingo.MessageProvider;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

public class ItemEjector extends JavaPlugin {

    private BukkitAudiences audiences;
    private MessageProvider messageProvider;
    private SettingsManager settingsManager;
    private DataSource dataSource;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        loadConfigAndMessages();

        dataSource = new SQLiteDataSource(this);
        playerManager = new PlayerManager(dataSource);

        registerEvents();
        registerCommands();

        // Load all players if server is not empty, to support /reload
        if (getServer().getOnlinePlayers().size() > 0) {
            getServer().getOnlinePlayers().forEach(player -> playerManager.loadPlayer(player.getUniqueId().toString()));
        }

        // Check for updates
        UpdateChecker.init(this, 75548).requestUpdateCheck().whenComplete((result, e) -> {
            if (result.requiresUpdate()) {
                getLogger().info(String.format("An update is available on Spigot: %s", result.getNewestVersion()));
                return;
            }

            UpdateChecker.UpdateReason reason = result.getReason();
            if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                this.getLogger().info(String.format("Your version of ItemEjector (%s) is up to date!", result.getNewestVersion()));
            }
            else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                this.getLogger().info(String.format("Your version of ItemEjector (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
            }
            else {
                this.getLogger().warning("Could not check for a new version of ItemEjector. Reason: " + reason);
            }
        });
    }

    public void loadConfigAndMessages() {
        getLogger().log(Level.INFO, "(Re)loading configs and messages");
        settingsManager = SettingsManagerBuilder.withYamlFile(new File(getDataFolder(), "config.yml"))
                .configurationData(Settings.class, GUISettings.class)
                .useDefaultMigrationService()
                .create();
        /*String languageCode = settingsManager.getProperty(Settings.LANGUAGE);

        if (languageCode == null) {
            getLogger().log(Level.WARNING, "A language code is not set in the config, falling back to default values");
            messages.useDefaults();
            return;
        }

        try {
            messages.load(languageCode);
        } catch (FileNotFoundException e) {
            getLogger().log(Level.SEVERE, "No language file matches the code \"" + languageCode + "\"!" +
                    " Please make one by copying the contents of lang_default.properties, or changing the config value to \"default\"." +
                    " DO NOT change the values in lang_default.properties!");
            getPluginLoader().disablePlugin(this);
            return;
        }*/

        audiences = BukkitAudiences.create(this);
        messageProvider = new MessageProvider(audiences, Messages.class, new File(getDataFolder(), "messages.yml"));
        messageProvider.setPrefix(Messages.PREFIX);

        getLogger().log(Level.INFO, "Loaded configs and messages");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerItemActionListener(this), this);
    }

    private void registerCommands() {
        PluginCommand command = Objects.requireNonNull(getCommand("itemejector"));
        command.setExecutor(new MainCommand(this));

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(this);
            try {
                MainCommand.registerCommodore(this, commodore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        playerManager.saveAllPlayers();
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}
