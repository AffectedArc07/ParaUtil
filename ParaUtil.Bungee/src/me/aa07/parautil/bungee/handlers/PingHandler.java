package me.aa07.parautil.bungee.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.aa07.parautil.bungee.ParaUtilBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingHandler implements Listener {
    public PingHandler(ParaUtilBungee plugin) {
        plugin.getLogger().info("[PingHandler] Enabling...");
        // Register the channel
        plugin.getProxy().registerChannel("aa:custom");
        // Register our events
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
        plugin.getLogger().info("[PingHandler] Enabled");
    }

    @EventHandler
    public void handleMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("aa:custom")) {
            return;
        }

        ByteArrayDataInput badi = ByteStreams.newDataInput(event.getData());
        String subchannel = badi.readUTF();
        switch (subchannel) {
            case "GetPing": {
                if (event.getReceiver() instanceof ProxiedPlayer) {
                    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                    ByteArrayDataOutput bado = ByteStreams.newDataOutput();
                    bado.writeUTF("SendPing");
                    bado.writeInt(player.getPing());
                    player.getServer().sendData("aa:custom", bado.toByteArray());
                }
                break;
            }

            default: {
                // Do nothing
                break;
            }
        }
    }
}
