package me.aa07.parautil.spigot.login;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import me.aa07.parautil.database.Tables;
import me.aa07.parautil.database.tables.records.LinkTokensRecord;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class LoginManager implements Listener {
    private ParaUtilSpigot plugin;
    private ConfigurationManager config;
    private DatabaseManager db;
    private PermissionsManager perms;

    public LoginManager(ParaUtilSpigot plugin, ConfigurationManager config, DatabaseManager db, PermissionsManager perms) {
        this.plugin = plugin;
        this.config = config;
        this.db = db;
        this.perms = perms;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("[LoginManager] Loaded");
    }

    /*
        RIGHT SO

        On login:
            If account is linked
                Does account have linked ckey?
                    Are they banned?
                        Deny access
                    No?
                        Are they an admin on the forums?
                            Allow access and grant perms
                        No?
                            Allow access
                No?
                    Deny access, tell them they need a linked ckey
            No?
                Deny access and tell them how to link
    */

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (config.generalConfiguration.devmode) {
            // Not on prod. Dont bother.
            return;
        }

        if (!config.databaseConfiguration.enabled) {
            // Not on prod. Dont bother.
            return;
        }

        if (!db.jooq().fetchExists(db.jooq().select(Tables.LINKED_ACCOUNTS.FUID).from(Tables.LINKED_ACCOUNTS).where(Tables.LINKED_ACCOUNTS.UUID.eq(event.getPlayer().getUniqueId().toString())))) {
            // They have no linked forum ID. Lets see if we have a link token waiting
            String link_token = null;

            if (db.jooq().fetchExists(db.jooq().select(Tables.LINK_TOKENS.TOKEN).from(Tables.LINK_TOKENS).where(Tables.LINK_TOKENS.UUID.eq(event.getPlayer().getUniqueId().toString())))) {
                // They do. Grab it.
                LinkTokensRecord ltr = db.jooq().selectFrom(Tables.LINK_TOKENS).where(Tables.LINK_TOKENS.UUID.eq(event.getPlayer().getUniqueId().toString())).fetchOne();
                link_token = ltr.getToken();
                ltr.setCreationTime(Timestamp.from(Instant.now().plusSeconds(60))); // 60 seconds expire
                ltr.update();
            } else {
                // They do not. Make one.
                Random random = new Random();
                link_token = String.format("%06d", random.nextInt(999999));
                LinkTokensRecord ltr = db.jooq().newRecord(Tables.LINK_TOKENS);
                ltr.setUuid(event.getPlayer().getUniqueId().toString());
                ltr.setToken(link_token);
                ltr.setIp(event.getRealAddress().getHostAddress());
                ltr.setCreationTime(Timestamp.from(Instant.now().plusSeconds(60))); // 60 seconds expire
                ltr.store();
            }

            // Create link
            String link = ChatColor.GOLD + "Open" + ChatColor.YELLOW + " https://paradise13.org/mc " + ChatColor.GOLD + "and enter the following code: " + ChatColor.GREEN + link_token;
            String warning = ChatColor.RED + "THIS TOKEN WILL EXPIRE IN 60 SECONDS. YOU WILL HAVE TO RELOG TO REGENERATE IT IF YOU DO NOT ENTER IT IN TIME";

            // Inform user
            String deny_message = ChatColor.RED + "" + ChatColor.BOLD + "Access Denied\n\n" + ChatColor.WHITE + "You need to link your Minecraft account and Paradise Forums accounts before you can play.\n" + link + "\n\n" + warning;

            event.disallow(Result.KICK_OTHER, deny_message);
            return;
        }
    }
}
