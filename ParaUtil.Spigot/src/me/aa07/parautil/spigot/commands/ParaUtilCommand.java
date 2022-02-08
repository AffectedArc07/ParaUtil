package me.aa07.parautil.spigot.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import me.aa07.parautil.database.Tables;
import me.aa07.parautil.database.tables.records.PlayersRecord;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import me.aa07.parautil.spigot.util.F;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jooq.Result;

public class ParaUtilCommand implements CommandExecutor, TabCompleter {
    private ConfigurationManager config;
    private DatabaseManager db;
    private PermissionsManager perms;
    private Properties buildVersion;

    public ParaUtilCommand(ParaUtilSpigot plugin, ConfigurationManager config, DatabaseManager db, PermissionsManager perms) {
        this.config = config;
        this.db = db;
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
            sender.sendMessage(ChatColor.GOLD + "/parautil lookup <mcname>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Performs a lookup on an MC username [Requires Admin]");
            sender.sendMessage(ChatColor.GOLD + "/parautil reload" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Reloads config & permissions");
            sender.sendMessage(ChatColor.GOLD + "/parautil version" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Gets the plugin version");
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

            case "lookup": {
                if (!sender.hasPermission("parautil.lookup")) {
                    sender.sendMessage(F.pri("Access Denied"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(F.pri("Usage: " + ChatColor.GOLD + "/parautil lookup <mcusername>"));
                    return true;
                }

                handleLookup(sender, args[1]);
                return true;
            }

            default: {
                sender.sendMessage(F.pri("Error", "Unrecognized subcommand"));
                return true;
            }
        }
    }

    private void handleLookup(CommandSender sender, String target) {
        // This can take a bit, so inform them
        sender.sendMessage(F.pri("Searching..."));
        // See if they exist
        if (!db.jooq().fetchExists(db.jooq().selectFrom(Tables.PLAYERS).where(Tables.PLAYERS.LAST_USERNAME.eq(target)))) {
            sender.sendMessage(F.pri("Player " + F.item(target) + " has not joined the server."));
            return;
        }

        Result<PlayersRecord> db_result = db.jooq().selectFrom(Tables.PLAYERS).where(Tables.PLAYERS.LAST_USERNAME.eq(target)).fetch();
        if (db_result.size() > 1) {
            sender.sendMessage(F.pri("Warning: Mutiple players returned"));
        }

        for (PlayersRecord record : db_result) {
            sender.sendMessage(F.pri("Info on " + F.item(record.getLastUsername())));
            sender.sendMessage(ChatColor.GOLD + "UUID" + ChatColor.WHITE + " - " + ChatColor.YELLOW + record.getUuid());
            sender.sendMessage(ChatColor.GOLD + "Ckey" + ChatColor.WHITE + " - " + ChatColor.YELLOW + record.getLastCkey());
            sender.sendMessage(ChatColor.GOLD + "First Seen" + ChatColor.WHITE + " - " + ChatColor.YELLOW + record.getFirstSeen());
            sender.sendMessage(ChatColor.GOLD + "Last Seen" + ChatColor.WHITE + " - " + ChatColor.YELLOW + record.getLastSeen());
        }

    }

    // Provide tab completion logic
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                List<String> list = new ArrayList<String>();
                list.add("lookup");
                list.add("reload");
                list.add("version");
                return list;
            }

            if (args.length == 2) {
                if (args[0].equals("lookup") && args[1].equals("")) {
                    List<String> list = new ArrayList<String>();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                    return list;
                }
            }

        }

        return new ArrayList<String>(); // Provide nothing
    }
}
