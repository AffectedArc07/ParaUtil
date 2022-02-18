package me.aa07.parautil.spigot.configuration;

import java.util.HashMap;
import java.util.Set;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.sections.ChatConfiguration;
import me.aa07.parautil.spigot.configuration.sections.DatabaseConfiguration;
import me.aa07.parautil.spigot.configuration.sections.DiscordConfiguration;
import me.aa07.parautil.spigot.configuration.sections.GeneralConfiguration;
import me.aa07.parautil.spigot.configuration.sections.PermissionsConfig;
import me.aa07.parautil.spigot.configuration.sections.WebConfiguration;
import org.bukkit.ChatColor;

public class ConfigurationManager {
    private ParaUtilSpigot plugin;
    public ChatConfiguration chatConfiguration;
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
        chatConfiguration = new ChatConfiguration();
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
        plugin.getConfig().addDefault("general.server_id", "aa_testing");

        plugin.getConfig().addDefault("general.force_allowed_users", new HashMap<String, String>() {{
                put("14b61d59-f16b-4763-836d-a65fa88c6641", "affectedarc07");
            }
        });

        // Defaults for chat
        plugin.getConfig().addDefault("chat.group_colours", new HashMap<String, String>() {{
                put("4", "GREEN");
            }
        });

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
        plugin.getConfig().addDefault("permissions.admin_nodes", new String[]{"plugin.permissions.*"});
        plugin.getConfig().addDefault("permissions.player_nodes", new String[]{"plugin.permissions.*"});

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
        generalConfiguration.serverId = plugin.getConfig().getString("general.server_id");

        Set<String> keys = plugin.getConfig().getConfigurationSection("general.force_allowed_users").getKeys(false);

        for (String key : keys) {
            String value = plugin.getConfig().getString(String.format("general.force_allowed_users.%s", key));
            if (value == null) {
                plugin.getLogger().warning(String.format("[ConfigurationManager] General force allowed users configuration specified an invalid ckey for UUID %s (null)", key));
                continue;
            }

            generalConfiguration.userMap.put(key, value);
        }

        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded %s user bypasses from config", generalConfiguration.userMap.size()));

        // Load Chat
        Set<String> keys2 = plugin.getConfig().getConfigurationSection("chat.group_colours").getKeys(false);

        for (String key : keys2) {
            String value = plugin.getConfig().getString(String.format("chat.group_colours.%s", key));
            if (value == null) {
                plugin.getLogger().warning(String.format("[ConfigurationManager] Chat group colours configuration specified an invalid colour for group %s (null)", key));
                continue;
            }

            ChatColor target_colour = null;
            try {
                target_colour = ChatColor.valueOf(value);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning(String.format("[ConfigurationManager] Chat group colours configuration specified an invalid colour for group %s (%s)", key, value));
                exception.printStackTrace();
                continue;
            }

            chatConfiguration.rankMap.put(key, target_colour);
        }

        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded %s rank colours from config", chatConfiguration.rankMap.size()));

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
        permissionsConfig.playerPermissions = plugin.getConfig().getStringList("permissions.player_nodes");
        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded %s player permissions from config", permissionsConfig.playerPermissions.size()));
        permissionsConfig.adminPermissions = plugin.getConfig().getStringList("permissions.admin_nodes");
        plugin.getLogger().info(String.format("[ConfigurationManager] Loaded %s admin permissions from config", permissionsConfig.adminPermissions.size()));

        // Load web
        webConfiguration.apiHost = plugin.getConfig().getString("web.apihost");
        webConfiguration.apiKey = plugin.getConfig().getString("web.apikey");
    }
}
