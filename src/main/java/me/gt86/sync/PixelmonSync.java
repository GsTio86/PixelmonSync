package me.gt86.sync;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.init.PixelmonInitEvent;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PixelmonStorageManager;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import me.gt86.sync.command.ReloadCommand;
import me.gt86.sync.config.Config;
import me.gt86.sync.connection.Database;
import me.gt86.sync.storage.StorageType;
import me.gt86.sync.storage.adapters.MySqlStorageSaveAdapter;
import me.gt86.sync.utils.ThreadUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PixelmonSync.MODID)
public class PixelmonSync {
    public static final String MOD_NAME = "PixelmonSync";
    public static final String MODID = "pixelmonsync";

    public static final Logger LOGGER = LogManager.getLogger();

    public PixelmonSync() {
        Config.initialize();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
    }

    @SubscribeEvent
    public void init(FMLCommonSetupEvent event) {
        LOGGER.info("Loading PixelmonSync ...");
        registerStorageType();
        MinecraftForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void postInit(FMLLoadCompleteEvent event) {
        ThreadUtil.runAsync(() -> {
            Database.init();
            LOGGER.info("PixelmonSync database initialized successfully.");
        });
    }

    @SubscribeEvent
    public void stoped(FMLServerStoppedEvent event) {
        ThreadUtil.runAsync(() -> {
            Database.stop();
            LOGGER.info("PixelmonSync database closed successfully.");
        });
    }

    @SubscribeEvent
    public void command(RegisterCommandsEvent event) {
        new ReloadCommand(event.getDispatcher());
    }

    private void registerStorageType() {
        StorageType.registerClass(PlayerPartyStorage.class, StorageType.POKEMON_STORAGE);
        StorageType.registerClass(PCStorage.class, StorageType.POKEMON_STORAGE_PC);
    }

    @SubscribeEvent
    public void onPixelmonInit(PixelmonInitEvent event) {
        PixelmonStorageManager storageManager = (PixelmonStorageManager) event.getStorageManager();
        storageManager.setSaveAdapter(new MySqlStorageSaveAdapter());
    }
}
