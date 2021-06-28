package me.loidsemus.itemejector;

import me.loidsemus.itemejector.database.DataPlayer;
import me.loidsemus.itemejector.database.DataSource;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private final DataSource dataSource;
    private final Map<String, DataPlayer> players = new HashMap<>();

    public PlayerManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataPlayer loadPlayer(String uuid) {
        DataPlayer dataPlayer = dataSource.loadPlayer(uuid);
        players.put(uuid, dataPlayer);
        return dataPlayer;
    }

    public void clearAndSavePlayer(String uuid) {
        dataSource.savePlayer(players.get(uuid));
        players.remove(uuid);
    }

    public DataPlayer getPlayer(String uuid) {
        return players.get(uuid);
    }

    public void saveAllPlayers() {
        players.forEach((uuid, dataPlayer) -> dataSource.savePlayer(dataPlayer));
    }
}
