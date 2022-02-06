package me.aa07.parautil.spigot.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.configuration.sections.PermissionsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionsManager implements Listener {
    private ParaUtilSpigot plugin;
    private PermissionsConfig config;
    private HashMap<Player, PermissionAttachment> attachments;

    public PermissionsManager(ParaUtilSpigot plugin, ConfigurationManager config) {
        this.plugin = plugin;
        this.config = config.permissionsConfig;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        attachments = new HashMap<Player, PermissionAttachment>();

        // ADD ALL PERMISSIONS THIS PLUGIN USES
        Bukkit.getPluginManager().addPermission(new Permission("parautil.admin")); // Permission for /parautil mc2ckey & such

        plugin.getLogger().info("[PermissionsManager] Loaded");
    }

    public void grantAdminPermissions(Player player) {
        for (String config_perm : config.adminPermissions) {
            List<String> perms = calculatePermissions(config_perm);
            PermissionAttachment pa = attachments.get(player);
            for (String perm : perms) {
                pa.setPermission(perm, true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PermissionAttachment pa = event.getPlayer().addAttachment(plugin);
        attachments.put(event.getPlayer(), pa);
    }

    @EventHandler
    public void onPlayerLeave(PlayerJoinEvent event) {
        attachments.remove(event.getPlayer());
    }

    private List<String> calculatePermissions(String perm) {
        List<String> output = new ArrayList<String>();

        if (perm.equals("*")) {
            // Its a wildcard. ADD EVERYTHING.
            for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
                output.add(permission.getName());
            }
            return output;
        }

        if (perm.contains("*")) {
            // Get the last *
            int last_asterisk = perm.lastIndexOf("*");
            // Split it
            String starting_node = perm.substring(0, last_asterisk);
            for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
                if (permission.getName().startsWith(starting_node)) {
                    output.add(permission.getName());
                }
            }

            return output;
        }

        // Just add it raw
        output.add(perm);

        return output;
    }
}
