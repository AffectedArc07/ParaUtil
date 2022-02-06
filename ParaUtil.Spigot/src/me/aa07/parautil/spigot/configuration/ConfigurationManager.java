package me.aa07.parautil.spigot.configuration;

import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.sections.DatabaseConfiguration;
import me.aa07.parautil.spigot.configuration.sections.GeneralConfiguration;
import me.aa07.parautil.spigot.configuration.sections.PermissionsConfig;
import me.aa07.parautil.spigot.configuration.sections.WebConfiguration;

public class ConfigurationManager {
    private ParaUtilSpigot plugin;
    public DatabaseConfiguration databaseConfiguration;
    public GeneralConfiguration generalConfiguration;
    public PermissionsConfig permissionsConfig;
    public WebConfiguration webConfiguration;

    public ConfigurationManager(ParaUtilSpigot plugin) {
        long start = System.currentTimeMillis();
        plugin.getLogger().info("[ConfigurationManager] Loading config");
        this.plugin = plugin;
        // Setup defaults in the plugin config
        setupDefaults();
        // Add our objects
        databaseConfiguration = new DatabaseConfiguration();
        generalConfiguration = new GeneralConfiguration();
        permissionsConfig = new PermissionsConfig();
        webConfiguration = new WebConfiguration();
        // Load them all
        loadConfig();
        long end = System.currentTimeMillis();
        long duration = end - start;
        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded in %sms", duration));
    }

    // Setup our defaults in the file to avoid NPEs, and give something to edit
    private void setupDefaults() {
        // General defaults
        plugin.getConfig().addDefault("general.devmode", true);

        // Defaults for the database
        plugin.getConfig().addDefault("database.enabled", false);
        plugin.getConfig().addDefault("database.host", "10.0.0.10:3306");
        plugin.getConfig().addDefault("database.username", "username");
        plugin.getConfig().addDefault("database.password", "password");
        plugin.getConfig().addDefault("database.db", "paradise_mc");

        // Defaults for permissions
        plugin.getConfig().addDefault("permissions.admingroupid", 11);
        plugin.getConfig().addDefault("permissions.grantednodes", new String[]{"plugin.permissions.*"});

        // Defaults for the web API
        plugin.getConfig().addDefault("web.apihost", "https://www.paradisestation.org/forum/custom/mc.php");
        plugin.getConfig().addDefault("web.apikey", "some_long_random_string_here");

        // Save them
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    // In its own method so it can be reloaded without the entire server being restarted
    // Modded servers can take upwards of 5+ minutes to start
    private void loadConfig() {
        // Load general
        generalConfiguration.devmode = plugin.getConfig().getBoolean("general.devmode");

        // Load DB
        databaseConfiguration.enabled = plugin.getConfig().getBoolean("database.enabled");
        databaseConfiguration.host = plugin.getConfig().getString("database.host");
        databaseConfiguration.username = plugin.getConfig().getString("database.username");
        databaseConfiguration.password = plugin.getConfig().getString("database.password");
        databaseConfiguration.db = plugin.getConfig().getString("database.db");

        // Load permissions
        permissionsConfig.adminGroupId = plugin.getConfig().getInt("permissions.admingroupid");
        permissionsConfig.adminPermissions = plugin.getConfig().getStringList("permissionslist.grantednodes");

        // Load web
        webConfiguration.apiHost = plugin.getConfig().getString("web.apihost");
        webConfiguration.apiKey = plugin.getConfig().getString("web.apikey");
    }
}
