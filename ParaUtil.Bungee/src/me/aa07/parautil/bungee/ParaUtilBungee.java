package me.aa07.parautil.bungee;

import me.aa07.parautil.bungee.handlers.PingHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class ParaUtilBungee extends Plugin {
    @Override
    public void onEnable() {
        getLogger().info("[Core] Starting...");
        // Setup the ping handler.
        new PingHandler(this);
        getLogger().info("[Core] Enabled");
    }
}
