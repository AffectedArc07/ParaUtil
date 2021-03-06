package me.aa07.parautil.spigot;

import me.aa07.parautil.spigot.commands.ParaUtilCommand;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.discord.DiscordManager;
import me.aa07.parautil.spigot.login.LoginManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import me.aa07.parautil.spigot.ping.PingManager;
import me.aa07.parautil.spigot.tablist.TablistManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ParaUtilSpigot extends JavaPlugin {
    // We need this in shutdown
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        getLogger().info("[Core] Starting up...");

        /* ===== REFERENCED MODULES ===== */

        // Setup ConfigurationManager
        ConfigurationManager configuration_manager = new ConfigurationManager(this);

        // Setup DatabaseManager
        DatabaseManager database_manager = new DatabaseManager(this, configuration_manager);

        // Setup PermissionsManagaer
        PermissionsManager permissions_manager = new PermissionsManager(this, configuration_manager);

        // Setup LoginManager
        LoginManager login_manager = new LoginManager(this, configuration_manager, database_manager, permissions_manager);

        // Setup DiscordManager
        discordManager = new DiscordManager(this, configuration_manager, login_manager);

        /* ===== NON REFERENCED MODULES ===== */

        // Setup PingManager
        new PingManager(this);

        // Setup TablistManager
        new TablistManager(this);

        /* ===== COMMANDS ===== */

        ParaUtilCommand command = new ParaUtilCommand(this, configuration_manager, database_manager, permissions_manager, discordManager);
        getCommand("parautil").setExecutor(command);
        getCommand("parautil").setTabCompleter(command);


        long duration = System.currentTimeMillis() - start;
        getLogger().info(String.format("[Core] Enabled in %sms", duration));
    }

    @Override
    public void onDisable() {
        discordManager.sendShutdown();
    }
}
