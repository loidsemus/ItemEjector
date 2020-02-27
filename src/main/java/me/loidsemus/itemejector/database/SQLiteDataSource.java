package me.loidsemus.itemejector.database;

import me.loidsemus.itemejector.ItemEjector;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.sql.*;
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
    private Connection connection;

    public SQLiteDataSource(ItemEjector plugin) {
        this.plugin = plugin;

        dbFile = new File(plugin.getDataFolder(), "data/players.db");
        try {
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            }
            connect();
            connection.createStatement().executeUpdate(CREATE_TABLE_QUERY);
        } catch (IOException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create database: " + e.getMessage());
        }
    }

    private void connect() throws SQLException {
        String url = "jdbc:sqlite:" + dbFile.getPath();
        connection = DriverManager.getConnection(url);
    }

    @Override
    public void savePlayer(DataPlayer player) {
        try (PreparedStatement deleteStmt = connection.prepareStatement(DELETE_PLAYER_QUERY)) {
            deleteStmt.setString(1, player.getUuid());
            deleteStmt.executeUpdate();

            for (Map.Entry<Material, Integer> entry : player.getBlacklistedItems().entrySet()) {
                PreparedStatement stmt = connection.prepareStatement(INSERT_PLAYER_QUERY);
                Material material = entry.getKey();
                int max = entry.getValue();
                stmt.setString(1, player.getUuid());
                stmt.setString(2, material.toString());
                stmt.setInt(3, max);
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataPlayer loadPlayer(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_PLAYER_QUERY)) {

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
