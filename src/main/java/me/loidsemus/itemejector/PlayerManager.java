package me.loidsemus.itemejector;

import me.loidsemus.itemejector.database.DataPlayer;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private ItemEjector plugin;
    private Map<String, DataPlayer> players = new HashMap<>();

    public PlayerManager(ItemEjector plugin) {
        this.plugin = plugin;
    }

    public DataPlayer loadPlayer(String uuid) {
        DataPlayer dataPlayer = plugin.getDataSource().loadPlayer(uuid);
        players.put(uuid, dataPlayer);
        return dataPlayer;
    }

    public void clearAndSavePlayer(String uuid) {
        plugin.getDataSource().savePlayer(players.get(uuid));
        players.remove(uuid);
    }

    public DataPlayer getPlayer(String uuid) {
        return players.get(uuid);
    }

    public void loadAllPlayers() {
        plugin.getServer().getOnlinePlayers().forEach(player -> loadPlayer(player.getUniqueId().toString()));
    }

    public void saveAllPlayers() {
        players.forEach((uuid, dataPlayer) -> plugin.getDataSource().savePlayer(dataPlayer));
    }
}
