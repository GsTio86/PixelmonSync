package me.gt86.sync.storage.adapters;

import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageSaveAdapter;
import me.gt86.sync.PixelmonSync;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.UUID;

import static me.gt86.sync.storage.mysql.StorageQueries.*;

public class MySqlStorageSaveAdapter implements StorageSaveAdapter {
    @Override
    public void save(PokemonStorage storage) {
        try {
            saveStorage(storage);
        } catch (Exception e) {
            PixelmonSync.LOGGER.error("Couldn't write player data file for " + storage.uuid.toString(), e);
        }
    }

    @Nonnull
    @Override
    public <T extends PokemonStorage> T load(UUID uuid, Class<T> clazz) {
        try {
            T storage = clazz.getConstructor(UUID.class).newInstance(uuid);
            if (!isExists(storage, uuid)) {
                return storage;
            } else {
                try {
                    CompoundNBT nbt = loadStorage(storage, uuid);
                    return (T) storage.readFromNBT(nbt);
                } catch (Exception e) {
                    PixelmonSync.LOGGER.error("Couldn't load player data file for " + uuid, e);
                    return clazz.getConstructor(UUID.class).newInstance(uuid);
                }
            }
        } catch (Exception e) {
            PixelmonSync.LOGGER.error("Failed to load storage! " + clazz.getSimpleName() + ", UUID: " + uuid.toString());
            e.printStackTrace();
            return null;
        }
    }
}
