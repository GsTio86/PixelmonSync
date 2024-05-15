package me.gt86.sync.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.gt86.sync.PixelmonSync;
import net.minecraft.util.DefaultUncaughtExceptionHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadUtil {

    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(5,
        new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("pixelmonsync_%d")
            .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(PixelmonSync.LOGGER)).build());

    public static void runAsync(Runnable runnable) {
        SCHEDULED_EXECUTOR_SERVICE.execute(runnable);
    }
}
