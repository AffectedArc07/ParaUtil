package me.aa07.parautil.spigot;

import me.aa07.parautil.spigot.commands.ParaUtilCommand;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.login.LoginManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import me.aa07.parautil.spigot.ping.PingManager;
import me.aa07.parautil.spigot.tablist.TablistManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ParaUtilSpigot extends JavaPlugin {

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

        // Setup VersionManager


        /* ===== NON REFERENCED MODULES ===== */

        // Setup LoginManager
        new LoginManager(this, configuration_manager, database_manager, permissions_manager);

        // Setup PingManager
        new PingManager(this);

        // Setup TablistManager
        new TablistManager(this);

        /* ===== COMMANDS ===== */

        getCommand("parautil").setExecutor(new ParaUtilCommand(this, configuration_manager, permissions_manager));


        long duration = System.currentTimeMillis() - start;
        getLogger().info(String.format("[Core] Enabled in %sms", duration));
    }
}
