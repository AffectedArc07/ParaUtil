package me.aa07.parautil.spigot;

import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.login.LoginManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
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

        // Setup LoginManager
        LoginManager login_manager = new LoginManager(this, configuration_manager, database_manager, permissions_manager);

        /* ===== NON REFERENCED MODULES ===== */

        // Setup ChatManager

        // Setup CommandsManager

        // Setup PingManager

        // Setup TablistManager

        long duration = System.currentTimeMillis() - start;
        getLogger().info(String.format("[Core] Enabled in %sms", duration));
    }
}
