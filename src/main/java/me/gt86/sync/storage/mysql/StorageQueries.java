package me.gt86.sync.storage.mysql;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import me.gt86.sync.connection.Database;
import me.gt86.sync.storage.StorageType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;

public class StorageQueries {

    private static final String POKEMON_STORAGE = StorageType.POKEMON_STORAGE.getName();
    private static final String POKEMON_STORAGE_PC = StorageType.POKEMON_STORAGE_PC.getName();

    public static String CREATE_PARTY_TABLE = "CREATE TABLE IF NOT EXISTS " + POKEMON_STORAGE + " (" +
                                              "uuid           CHAR(36)        NOT NULL," +
                                              "nbt            LONGBLOB                ," +
                                              "PRIMARY KEY(uuid));";

    public static String CREATE_PC_TABLE = "CREATE TABLE IF NOT EXISTS " + POKEMON_STORAGE_PC + " (" +
                                           "uuid           CHAR(36)        NOT NULL," +
                                           "nbt            LONGBLOB                ," +
                                           "PRIMARY KEY(uuid));";

    private static String createStorage(PokemonStorage storage) {
        return "INSERT INTO " + StorageType.getType(storage.getClass()).getName() + " VALUES (?, ?) ON DUPLICATE KEY UPDATE nbt = ?";
    }

    private static String updateStorage(PokemonStorage storage) {
        return "UPDATE " + StorageType.getType(storage.getClass()).getName() + " SET nbt = ? WHERE uuid = ?";
    }
    private static String isExists(PokemonStorage storage) {
        return "SELECT EXISTS(SELECT 1 FROM " + StorageType.getType(storage.getClass()).getName() + " WHERE uuid = ?) AS exist";
    }

    private static String loadStorage(PokemonStorage storage) {
        return "SELECT * FROM " + StorageType.getType(storage.getClass()).getName() + " WHERE uuid = ?";
    }

    public static void createStorage(Connection con,PokemonStorage storage, UUID uuid) throws SQLException {
        String nbt = storage.writeToNBT(new CompoundNBT()).toString();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(nbt.getBytes());
        try (PreparedStatement statement = con.prepareStatement(createStorage(storage))) {
            statement.setString(1, uuid.toString());
            statement.setBlob(2, byteArrayInputStream);
            statement.setBlob(3, byteArrayInputStream);
            statement.executeUpdate();
        }
    }

    public static CompoundNBT loadStorage(PokemonStorage storage, UUID uuid) throws SQLException, CommandSyntaxException {
        CompoundNBT nbt = new CompoundNBT();
        try (Connection con = Database.getConnection(); PreparedStatement statement = con.prepareStatement(loadStorage(storage))) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    final Blob blob = resultSet.getBlob("nbt");
                    if (blob != null) {
                        byte[] bytes = blob.getBytes(1, (int) blob.length());
                        blob.free();
                        String data = new String(bytes, StandardCharsets.UTF_8);
                        nbt = JsonToNBT.parseTag(data);
                    }
                }
            }
        }
        return nbt;
    }

    public static void updateStorage(Connection con, PokemonStorage storage, UUID uuid) throws SQLException {
        try (PreparedStatement statement = con.prepareStatement(updateStorage(storage))) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(storage.writeToNBT(new CompoundNBT()).toString().getBytes());
            statement.setBlob(1, byteArrayInputStream);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        }
    }

    public static void saveStorage(PokemonStorage storage) throws SQLException {
        UUID uuid = storage.uuid;
        try (Connection con = Database.getConnection()) {
            if (!isExists(con, storage, uuid)) {
                createStorage(con, storage, uuid);
            }
            updateStorage(con, storage, uuid);
        }
    }

    public static boolean isExists(Connection con, PokemonStorage storage, UUID uuid) throws SQLException {
        boolean exists = false;
        try (PreparedStatement statement = con.prepareStatement(isExists(storage))) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exists = resultSet.getBoolean("exist");
                }
            }
        }
        return exists;
    }

    public static boolean isExists(PokemonStorage storage, UUID uuid) throws SQLException {
        boolean exists = false;
        try (Connection con = Database.getConnection(); PreparedStatement statement = con.prepareStatement(isExists(storage))) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    exists = resultSet.getBoolean("exist");
                }
            }
        }
        return exists;
    }

}
