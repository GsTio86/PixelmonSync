package me.gt86.sync.connection;

import com.zaxxer.hikari.HikariDataSource;
import me.gt86.sync.PixelmonSync;
import me.gt86.sync.config.Config;
import me.gt86.sync.storage.mysql.StorageQueries;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static HikariDataSource hikariDataSource;

    public static void init() {
        stop();
        hikariDataSource = new HikariDataSource(Config.createHikariConfig());
        createStorageTable();
    }

    public static void createStorageTable() {
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute(StorageQueries.CREATE_PARTY_TABLE);
            stmt.execute(StorageQueries.CREATE_PC_TABLE);
            PixelmonSync.sendDebugMessage("Tables created successfully");
        } catch (SQLException e) {
            PixelmonSync.LOGGER.error("Error while creating table", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (hikariDataSource == null) {
            init();
        }
        return hikariDataSource.getConnection();
    }

    public static void stop() {
        if (hikariDataSource != null) {
            try {
                hikariDataSource.close();
                PixelmonSync.sendDebugMessage("Connection closed successfully");
            } catch (Exception e) {
                PixelmonSync.LOGGER.error("Error while closing connection", e);
            }
        }
    }
}

