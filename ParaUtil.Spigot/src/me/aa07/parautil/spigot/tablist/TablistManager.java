package me.aa07.parautil.spigot.tablist;

import java.lang.reflect.Field;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TablistManager implements Runnable {
    protected ParaUtilSpigot plugin;
    private PacketPlayOutPlayerListHeaderFooter packet;
    private Field h;
    private Field f;

    public TablistManager(ParaUtilSpigot plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("[TablistManager] Attempting to setup reflection");

        try {
            packet = new PacketPlayOutPlayerListHeaderFooter();
            h = packet.getClass().getDeclaredField("a");
            h.setAccessible(true);
            f = packet.getClass().getDeclaredField("b");
            f.setAccessible(true);
        } catch (Exception e) {
            plugin.getLogger().warning("[TablistManager] Failed to setup reflection!");
            e.printStackTrace();
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 5L, 100L);

        plugin.getLogger().info("[TablistManager] Done");
    }

    private String formatTps(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    @Override
    public void run() {
        TextComponent header = new TextComponent("\n" + ChatColor.GOLD + ChatColor.BOLD + "Paradise Minecraft" + "\n");
        Runtime r = Runtime.getRuntime();
        String player_string = ChatColor.GRAY + "Players: " + ChatColor.GOLD + Bukkit.getServer().getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.GOLD + Bukkit.getServer().getMaxPlayers() + ChatColor.GRAY;
        String tps_string = ChatColor.GRAY + "TPS: " + formatTps(getTps()) + ChatColor.GRAY;
        String ram_string = ChatColor.GRAY + "RAM: " + ChatColor.GOLD + ((r.totalMemory() - r.freeMemory()) / 1048576) + "MB" + ChatColor.GRAY + " / " + ChatColor.GOLD + (r.maxMemory() / 1048576) + "MB";
        String support_string = "\n" + ChatColor.AQUA + "" + ChatColor.ITALIC + "   Contact affected on discord if something is wrong   ";
        TextComponent footer = new TextComponent(String.format("\n%s | %s | %s\n%s\n", player_string, tps_string, ram_string, support_string));

        sendTablistData(header, footer);
    }

    @SuppressWarnings({"deprecation"})
    private double getTps() {
        return MinecraftServer.getServer().recentTps[0];
    }

    private void sendTablistData(TextComponent header, TextComponent footer) {
        try {
            h.set(packet, IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(header)));
            f.set(packet, IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(footer)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
