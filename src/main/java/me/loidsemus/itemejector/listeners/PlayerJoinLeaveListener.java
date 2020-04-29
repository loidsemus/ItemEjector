package me.loidsemus.itemejector.listeners;

import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private ItemEjector plugin;

    public PlayerJoinLeaveListener(ItemEjector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        plugin.getPlayerManager().loadPlayer(uuid);
        //System.out.println(plugin.getPlayerManager().getPlayer(uuid).getBlacklistedItems());

        // Notify operators about new version
        /*if(event.getPlayer().isOp() && plugin.getNewVersion() != null) {
            event.getPlayer().sendMessage(plugin.getMessages().getMessage("prefix", false) + " There is a new version available on SpigotMC: "
            + plugin.getNewVersion() + " (you're on " + plugin.getDescription().getVersion() + ")");
        }*/
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        plugin.getPlayerManager().clearAndSavePlayer(uuid);
    }

}
