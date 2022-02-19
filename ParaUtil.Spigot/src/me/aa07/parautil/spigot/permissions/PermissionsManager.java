package me.aa07.parautil.spigot.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.configuration.sections.PermissionsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionsManager implements Listener {
    private ParaUtilSpigot plugin;
    private PermissionsConfig config;
    private HashMap<Player, PermissionAttachment> attachments;
    private HashSet<Player> admins;
    private boolean enabled;

    public PermissionsManager(ParaUtilSpigot plugin, ConfigurationManager config) {
        this.plugin = plugin;
        this.config = config.permissionsConfig;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        attachments = new HashMap<Player, PermissionAttachment>();
        admins = new HashSet<Player>();

        // ADD ALL PERMISSIONS THIS PLUGIN USES
        Bukkit.getPluginManager().addPermission(new Permission("parautil.lookup"));

        if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null) {
            enabled = false;
            plugin.getLogger().info("[PermissionsManager] PermissionsEx detected. Events disabled.");
        } else {
            enabled = true;
            plugin.getLogger().info("[PermissionsManager] Loaded");
        }

    }

    // Called from LoginMananger
    public void grantAdminPermissions(Player player) {
        if (!enabled) {
            return;
        }
        admins.add(player);
    }

    // Called from the reload command
    public void refreshAll() {
        if (!enabled) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPermissions(player);
        }
    }

    public void refreshPermissions(Player player) {
        if (!enabled) {
            return;
        }
        PermissionAttachment pa = attachments.get(player);
        // Remove all old
        for (String perm : pa.getPermissions().keySet()) {
            pa.unsetPermission(perm);
        }

        // Add regular permissions
        for (String config_perm : config.playerPermissions) {
            List<String> perms = calculatePermissions(config_perm);
            for (String perm : perms) {
                pa.setPermission(perm, true);
            }
        }

        // Add new admin permissions
        if (admins.contains(player)) {
            for (String config_perm : config.adminPermissions) {
                List<String> perms = calculatePermissions(config_perm);
                for (String perm : perms) {
                    pa.setPermission(perm, true);
                }
            }
        }
    }

    public void addAttachment(Player player) {
        if (!enabled) {
            return;
        }
        PermissionAttachment pa = player.addAttachment(plugin);
        attachments.put(player, pa);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!enabled) {
            return;
        }
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

    public boolean isAdmin(Player player) {
        return admins.contains(player);
    }
}
