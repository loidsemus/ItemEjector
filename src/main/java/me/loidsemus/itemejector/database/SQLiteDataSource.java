package me.loidsemus.itemejector.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

public class SQLiteDataSource extends DataSource {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS players " +
            "(id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, uuid TEXT, item TEXT, max INTEGER);";
    private static final String INSERT_PLAYER_QUERY = "INSERT INTO players (uuid, item, max) VALUES (?, ?, ?);";
    private static final String SELECT_PLAYER_QUERY = "SELECT * FROM players WHERE uuid = ?;";
    private static final String DELETE_PLAYER_QUERY = "DELETE FROM players WHERE uuid = ?";

    private ItemEjector plugin;
    private File dbFile;
    private HikariDataSource hikari;

    public SQLiteDataSource(ItemEjector plugin) {
        this.plugin = plugin;

        dbFile = new File(plugin.getDataFolder(), "data/players.db");
        try {
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create database file: " + e.getMessage());
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("ItemEjectorSQLitePool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getPath());
        config.setMaximumPoolSize(10);
        config.setConnectionTestQuery("SELECT 1;");
        hikari = new HikariDataSource(config);

        try (Connection conn = hikari.getConnection(); PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE_QUERY)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayer(DataPlayer player) {
        try (Connection conn = hikari.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_PLAYER_QUERY)) {
            stmt.setString(1, player.getUuid());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Map.Entry<Material, Integer> entry : player.getBlacklistedItems().entrySet()) {
            try (Connection conn = hikari.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_PLAYER_QUERY)) {
                Material material = entry.getKey();
                int max = entry.getValue();
                stmt.setString(1, player.getUuid());
                stmt.setString(2, material.toString());
                stmt.setInt(3, max);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataPlayer loadPlayer(String uuid) {
        try (Connection conn = hikari.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PLAYER_QUERY)) {

            stmt.setString(1, uuid);
            ResultSet resultSet = stmt.executeQuery();
            DataPlayer player = new DataPlayer(uuid);
            while (resultSet.next()) {
                player.addOrUpdateBlacklistedItem(Material.getMaterial(resultSet.getString("item")), resultSet.getInt("max"));
            }
            return player;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
