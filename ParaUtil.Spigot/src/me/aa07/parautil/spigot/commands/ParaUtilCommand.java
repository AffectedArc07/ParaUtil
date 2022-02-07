package me.aa07.parautil.spigot.commands;

import java.io.IOException;
import java.util.Properties;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import me.aa07.parautil.spigot.util.F;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParaUtilCommand implements CommandExecutor {
    private ConfigurationManager config;
    private PermissionsManager perms;
    private Properties buildVersion;

    public ParaUtilCommand(ParaUtilSpigot plugin, ConfigurationManager config, PermissionsManager perms) {
        this.config = config;
        this.perms = perms;

        // Load build properties in this command
        buildVersion = new Properties();

        try {
            buildVersion.load(this.getClass().getResourceAsStream("/version.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("[ParaUtilCommand] Loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(F.pri("Command help"));
            sender.sendMessage(ChatColor.GOLD + "/parautil version" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Gets the plugin version");
            sender.sendMessage(ChatColor.GOLD + "/parautil reload" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Reloads config & permissions");
            return true;
        }

        switch (args[0]) {
            case "version": {
                String git = buildVersion.getProperty("build.git", "Unknown");
                String date = buildVersion.getProperty("build.date", "Unknown");
                String user = buildVersion.getProperty("build.user", "Unknown");

                sender.sendMessage(F.pri("Plugin Info"));
                sender.sendMessage(ChatColor.GOLD + "Commit" + ChatColor.WHITE + " - " + ChatColor.YELLOW + git);
                sender.sendMessage(ChatColor.GOLD + "Date" + ChatColor.WHITE + " - " + ChatColor.YELLOW + date);
                sender.sendMessage(ChatColor.GOLD + "User" + ChatColor.WHITE + " - " + ChatColor.YELLOW + user);
                return true;
            }

            case "reload": {
                if ((sender instanceof Player) && !sender.isOp()) {
                    sender.sendMessage(F.pri("Access Denied"));
                    return true;
                }

                config.loadConfig();
                perms.refreshAll();
                sender.sendMessage(F.pri("Reloaded config & permissions"));
                return true;
            }

            default: {
                sender.sendMessage(F.pri("Error", "Unrecognized subcommand"));
                break;
            }
        }

        return false;
    }

}
