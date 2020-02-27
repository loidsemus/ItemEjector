package me.loidsemus.itemejector.database;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

public class DataPlayer {

    private String uuid;
    private Map<Material, Integer> blacklistedItems;

    public DataPlayer(String uuid) {
        this.uuid = uuid;
        this.blacklistedItems = new EnumMap<>(Material.class);
    }

    public String getUuid() {
        return uuid;
    }

    public Map<Material, Integer> getBlacklistedItems() {
        return blacklistedItems;
    }

    public void addOrUpdateBlacklistedItem(Material item, int max) {
        blacklistedItems.put(item, max);
    }

    public void removeBlacklistedItem(Material item) {
        blacklistedItems.remove(item);
    }

    public boolean hasBlacklistedItem(Material item) {
        return blacklistedItems.containsKey(item);
    }
}
