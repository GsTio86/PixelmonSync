package me.gt86.sync.config;

import com.zaxxer.hikari.HikariConfig;
import me.gt86.sync.PixelmonSync;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.TimeUnit;

public class Config {

    private static final Common COMMON;
    private static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }
    public static void initialize() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    public static class Common {

        public final ForgeConfigSpec.ConfigValue<String> mysqlHost;
        public final ForgeConfigSpec.IntValue mysqlPort;
        public final ForgeConfigSpec.ConfigValue<String> mysqlDatabase;
        public final ForgeConfigSpec.ConfigValue<String> mysqlUsername;
        public final ForgeConfigSpec.ConfigValue<String> mysqlPassword;
        public final ForgeConfigSpec.BooleanValue useSSL;

        public final ForgeConfigSpec.ConfigValue<Integer> maxPoolSize;
        public final ForgeConfigSpec.ConfigValue<Integer> maxLifeTimeSeconds;
        public final ForgeConfigSpec.ConfigValue<Integer> maxIdleTimeSeconds;
        public final ForgeConfigSpec.ConfigValue<Integer> connectionTimeoutSeconds;


        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("MySQL Settings");
            mysqlHost = builder.define("mysqlHost", "localhost");
            mysqlPort = builder.defineInRange("mysqlPort", 3306, 1, 65535);
            mysqlDatabase = builder.define("mysqlDatabase", "pixelmon");
            mysqlUsername = builder.define("mysqlUsername", "root");
            mysqlPassword = builder.define("mysqlPassword", "password");
            useSSL = builder.define("useSSL", false);
            builder.pop();
            builder.push("HikariCP Settings");
            maxPoolSize = builder.define("maxPoolSize", 10);
            maxLifeTimeSeconds = builder.define("maxLifeTimeSeconds", 1800);
            maxIdleTimeSeconds = builder.define("maxIdleTimeSeconds", 600);
            connectionTimeoutSeconds = builder.define("connectionTimeoutSeconds", 30);
            builder.pop();
        }
    }

    private static final String DRIVERS = "com.mysql.cj.jdbc.Driver";
    private static final String OLD_DRIVERS = "com.mysql.jdbc.Driver";

    public static HikariConfig createHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(PixelmonSync.MOD_NAME + "-Pool");
        hikariConfig.setJdbcUrl("jdbc:mysql://" + COMMON.mysqlHost.get() + ":" + COMMON.mysqlPort.get() + "/" + COMMON.mysqlDatabase.get() + "?autoReconnect=true" + (COMMON.useSSL.get() ? "&useSSL=true" : "&useSSL=false"));
        hikariConfig.setUsername(COMMON.mysqlUsername.get());
        hikariConfig.setPassword(COMMON.mysqlPassword.get());
        try {
            Class.forName(DRIVERS);
            hikariConfig.setDriverClassName(DRIVERS);
        } catch (ClassNotFoundException ignored) {
            hikariConfig.setDriverClassName(OLD_DRIVERS);
        }

        hikariConfig.setMaximumPoolSize(Math.max(1, COMMON.maxPoolSize.get()));
        hikariConfig.setMaxLifetime(TimeUnit.SECONDS.toMillis(COMMON.maxLifeTimeSeconds.get()));
        hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(COMMON.connectionTimeoutSeconds.get()));
        hikariConfig.setIdleTimeout(TimeUnit.SECONDS.toMillis(COMMON.maxIdleTimeSeconds.get()));

        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheCallableStmts", true);
        hikariConfig.addDataSourceProperty("alwaysSendSetIsolation", false);
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", true);
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", true);
        hikariConfig.addDataSourceProperty("useLocalSessionState", true);

        hikariConfig.setConnectionTestQuery("/* Ping */ SELECT 1");
        return hikariConfig;
    }
}
