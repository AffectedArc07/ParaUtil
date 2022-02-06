package me.aa07.parautil.spigot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.Date;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.configuration.sections.DatabaseConfiguration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class DatabaseManager {
    private ParaUtilSpigot plugin;
    private DatabaseConfiguration config;

    public DatabaseManager(ParaUtilSpigot plugin, ConfigurationManager config) {
        this.plugin = plugin;
        this.config = config.databaseConfiguration;
        // Suppress JOOQ console spam
        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("org.jooq.no-tips", "true");

        plugin.getLogger().info("[DatabaseManager] Ready to handle DB requests");
    }

    // Get a DSL context
    public DSLContext jooq() {
        if (!config.enabled) {
            plugin.getLogger().warning("Attempted to get a DB Connection whilst the DB was disabled!");
            Thread.dumpStack();
            return null;
        }

        try {
            Connection dbcon = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", config.host, config.db), config.username, config.password);
            return DSL.using(dbcon, SQLDialect.MYSQL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Timestamp now() {
        return new Timestamp(new Date().getTime());
    }

}
