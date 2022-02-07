package me.aa07.parautil.spigot.ping;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.HashMap;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PingManager implements Runnable, PluginMessageListener, Listener {

    private HashMap<Player, Integer> pingmap;
    private Objective pingObjective;
    private ParaUtilSpigot plugin;

    public PingManager(ParaUtilSpigot plugin) {
        this.plugin = plugin;

        // Setup listener
        // We are using bungee, lets register the channel
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "aa:custom");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "aa:custom", this);
        pingmap = new HashMap<Player, Integer>();


        // Setup scoreboard
        pingObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Ping");
        if (pingObjective == null) {
            pingObjective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("Ping", "dummy");
            pingObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 5L, 100L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("[PingManager] Loaded");
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public void run() {
        ByteArrayDataOutput bado = ByteStreams.newDataOutput();
        bado.writeUTF("GetPing");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPluginMessage(plugin, "aa:custom", bado.toByteArray());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            pingObjective.getScore(player).setScore(pingmap.get(player));
        }
    }

    @EventHandler
    public void addPlayer(PlayerJoinEvent event) {
        pingmap.put(event.getPlayer(), 0); // You have the best connection
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("aa:custom")) {
            return; // NOT FOR US
        }

        ByteArrayDataInput badi = ByteStreams.newDataInput(message);
        String subchannel = badi.readUTF();
        if (!subchannel.equals("SendPing")) {
            // Not what we want
            return;
        }
        int ping = badi.readInt();
        pingmap.put(player, ping);
    }
}
