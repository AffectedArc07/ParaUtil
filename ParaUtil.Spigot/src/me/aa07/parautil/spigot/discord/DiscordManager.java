package me.aa07.parautil.spigot.discord;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

// This holds the actual management and interfacing with the discord bot and bukkit
public class DiscordManager implements Listener {
    private ParaUtilSpigot plugin;
    private ConfigurationManager config;
    private LoginManager loginManager;
    private Object startupLock;
    private DiscordBot bot;
    private Thread botThread;
    private boolean ready = false;
    private HashMap<Long, String> id2ckeyMap;

    public DiscordManager(ParaUtilSpigot plugin, ConfigurationManager config, LoginManager loginManager) {
        this.plugin = plugin;
        this.config = config;
        this.loginManager = loginManager;

        startupLock = new Object();
        id2ckeyMap = new HashMap<Long, String>();

        if (!this.config.discordConfiguration.enabled) {
            plugin.getLogger().info("[DiscordManager] Discord bot not enabled in config");
            return;
        }

        plugin.getLogger().info("[DiscordManager] Starting bot...");

        botThread = new Thread(this::launchBot, "ParaUtil.Spigot.Thread.DiscordBot");
        botThread.start();
        try {
            synchronized (startupLock) {
                while (!ready) {
                    startupLock.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        bot.sendMessage("✅ Server Started");
        plugin.getLogger().info("[DiscordManager] Ready!");
    }

    public void sendShutdown() {
        if (config.discordConfiguration.enabled) {
            bot.sendMessage("❎ Server Stopped");
        }
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        bot.sendChatMessage(loginManager.ckey(event.getPlayer()), event.getMessage());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        bot.sendMessage(String.format("▶ `%s` joined", loginManager.ckey(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        bot.sendMessage(String.format("◀ `%s` left", loginManager.ckey(event.getPlayer())));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        String playername = event.getEntity().getDisplayName();
        message = message.replace(playername, String.format("`%s`", playername)); // Encase the playername in a formatting block
        message = ChatColor.stripColor(message); // Strip out §
        bot.sendMessage(String.format("💀 %s", message));
    }

    // This is launched in a thread. DO NOT CALL THIS OUTSIDE OF A CUSTOM THREAD
    private void launchBot() {
        bot = new DiscordBot(plugin, this, config);
        ready = true;
        // Notify the other thread we launched
        synchronized (startupLock) {
            startupLock.notify();
        }
    }

    public void clearCache() {
        // Reset this
        id2ckeyMap.clear();
    }

    public void broadcastMessage(long sourceId, String message) {
        if (message.length() == 0) {
            // Dont bother
        }

        // First see if we have their ID caches
        if (!id2ckeyMap.containsKey(sourceId)) {
            // Get their ckey from their ID
            String request_url = String.format("%s?k=%s&i=%s", config.discordConfiguration.ckeyApiUrl, config.discordConfiguration.ckeyApiKey, sourceId);
            try {
                URL url = new URL(request_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                con.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuffer content = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                con.disconnect();
                String data = content.toString();

                Gson gson = new Gson();
                Id2CkeyResponseModel response = gson.fromJson(data, Id2CkeyResponseModel.class);

                if (response.success) {
                    id2ckeyMap.put(sourceId, response.data);
                } else {
                    id2ckeyMap.put(sourceId, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        String speaking_ckey = id2ckeyMap.get(sourceId);

        if (speaking_ckey == null) {
            return; // Null
        }

        Bukkit.broadcastMessage(String.format("%sDiscord %s%s %s%s", ChatColor.AQUA, ChatColor.GRAY, speaking_ckey, ChatColor.WHITE, message));
    }
}
