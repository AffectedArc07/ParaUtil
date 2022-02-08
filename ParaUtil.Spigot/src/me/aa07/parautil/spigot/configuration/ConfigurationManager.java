package me.aa07.parautil.spigot.configuration;

import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.sections.DatabaseConfiguration;
import me.aa07.parautil.spigot.configuration.sections.DiscordConfiguration;
import me.aa07.parautil.spigot.configuration.sections.GeneralConfiguration;
import me.aa07.parautil.spigot.configuration.sections.PermissionsConfig;
import me.aa07.parautil.spigot.configuration.sections.WebConfiguration;

public class ConfigurationManager {
    private ParaUtilSpigot plugin;
    public DatabaseConfiguration databaseConfiguration;
    public DiscordConfiguration discordConfiguration;
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
        discordConfiguration = new DiscordConfiguration();
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

        // Defaults for discord stuff
        plugin.getConfig().addDefault("discord.enabled", false);
        plugin.getConfig().addDefault("discord.token", "your_token_here");
        plugin.getConfig().addDefault("discord.channel", "channel_id_as_string");
        plugin.getConfig().addDefault("discord.ckeyapiurl", "https://www.paradisestation.org/forum/custom/id2ckey.php");
        plugin.getConfig().addDefault("discord.ckeyapikey", "some_long_random_string_here");

        // Defaults for permissions
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
    public void loadConfig() {
        // Load general
        generalConfiguration.devmode = plugin.getConfig().getBoolean("general.devmode");

        // Load DB
        databaseConfiguration.enabled = plugin.getConfig().getBoolean("database.enabled");
        databaseConfiguration.host = plugin.getConfig().getString("database.host");
        databaseConfiguration.username = plugin.getConfig().getString("database.username");
        databaseConfiguration.password = plugin.getConfig().getString("database.password");
        databaseConfiguration.db = plugin.getConfig().getString("database.db");

        // Load discord
        discordConfiguration.enabled = plugin.getConfig().getBoolean("discord.enabled");
        discordConfiguration.token = plugin.getConfig().getString("discord.token");
        discordConfiguration.channel = plugin.getConfig().getString("discord.channel");
        discordConfiguration.ckeyApiUrl = plugin.getConfig().getString("discord.ckeyapiurl");
        discordConfiguration.ckeyApiKey = plugin.getConfig().getString("discord.ckeyapikey");

        // Load permissions
        permissionsConfig.adminPermissions = plugin.getConfig().getStringList("permissions.grantednodes");
        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded %s permissions from config", permissionsConfig.adminPermissions.size()));

        // Load web
        webConfiguration.apiHost = plugin.getConfig().getString("web.apihost");
        webConfiguration.apiKey = plugin.getConfig().getString("web.apikey");
    }
}
