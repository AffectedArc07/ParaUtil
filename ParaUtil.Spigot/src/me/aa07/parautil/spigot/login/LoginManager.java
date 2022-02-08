package me.aa07.parautil.spigot.login;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import me.aa07.parautil.database.Tables;
import me.aa07.parautil.database.tables.records.LinkTokensRecord;
import me.aa07.parautil.database.tables.records.PlayersRecord;
import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import me.aa07.parautil.spigot.database.DatabaseManager;
import me.aa07.parautil.spigot.permissions.PermissionsManager;
import me.aa07.parautil.spigot.util.F;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginManager implements Listener {
    private ConfigurationManager config;
    private DatabaseManager db;
    private PermissionsManager perms;

    public LoginManager(ParaUtilSpigot plugin, ConfigurationManager config, DatabaseManager db, PermissionsManager perms) {
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

        // Wrap all this in try/catch
        try {
            if (!db.jooq().fetchExists(db.jooq().select(Tables.LINKED_ACCOUNTS.FUID).from(Tables.LINKED_ACCOUNTS).where(Tables.LINKED_ACCOUNTS.UUID.eq(event.getPlayer().getUniqueId().toString())))) {
                // They have no linked forum ID. Lets see if we have a link token waiting
                String link_token = null;

                if (db.jooq().fetchExists(db.jooq().select(Tables.LINK_TOKENS.TOKEN).from(Tables.LINK_TOKENS).where(Tables.LINK_TOKENS.UUID.eq(event.getPlayer().getUniqueId().toString())))) {
                    // They do. Grab it.
                    LinkTokensRecord ltr = db.jooq().selectFrom(Tables.LINK_TOKENS).where(Tables.LINK_TOKENS.UUID.eq(event.getPlayer().getUniqueId().toString())).fetchOne();
                    link_token = ltr.getToken();
                    ltr.setCreationTime(Timestamp.from(Instant.now().plusSeconds(60))); // 60 seconds expire
                    ltr.store();
                } else {
                    // They do not. Make one.
                    Random random = new Random();
                    link_token = String.format("%06d", random.nextInt(999999));
                    LinkTokensRecord ltr = db.jooq().newRecord(Tables.LINK_TOKENS);
                    ltr.setUuid(event.getPlayer().getUniqueId().toString());
                    ltr.setToken(link_token);
                    ltr.setIp(event.getAddress().getHostAddress());
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

            // If we are here, they do have a linked account, so we can make our web query now
            long fuid = db.jooq().select(Tables.LINKED_ACCOUNTS.FUID).from(Tables.LINKED_ACCOUNTS).where(Tables.LINKED_ACCOUNTS.UUID.eq(event.getPlayer().getUniqueId().toString())).fetchOne().value1();

            String request_url = String.format("%s?k=%s&u=%s", config.webConfiguration.apiHost, config.webConfiguration.apiKey, fuid);

            // You cant use HttpRequest here because the plugin runs on Java 8, and HttpRequest was added in Java 11
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
            LoginModel lm = gson.fromJson(data, LoginModel.class);

            if (!lm.canLogin) {
                String kick_reason = ChatColor.RED + "" + ChatColor.BOLD + "Access Denied\n\n" + ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + lm.reason + "\n\n" + ChatColor.AQUA + ChatColor.ITALIC + "If you believe this is wrong, please contact AffectedArc07.";
                event.disallow(Result.KICK_OTHER,  kick_reason);
                return;
            }

            // If we are here we can login

            // If they are an admin, mark them as so
            if (lm.isAdmin) {
                perms.addAttachment(event.getPlayer());
                perms.grantAdminPermissions(event.getPlayer());
            }

            // Set their displayname to their ckey
            event.getPlayer().setDisplayName(lm.ckey);
            // Log info
            logPlayerToDb(event.getPlayer(), lm.ckey);

        } catch (Exception e) {
            // Deny on failure
            e.printStackTrace();
            event.disallow(Result.KICK_OTHER, "There was a problem logging you in. If this persists, please inform AffectedArc07.");
            return;
        }
    }

    // Fires on successful logins
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (perms.isAdmin(event.getPlayer())) {
            event.getPlayer().sendMessage(F.pri("You have been granted admin rights ingame."));
        }
    }

    // Updates player name, ckey and lastseen time in the DB
    private void logPlayerToDb(Player player, String ckey) {
        // See if they exist first
        if (db.jooq().fetchExists(db.jooq().select(Tables.PLAYERS.UUID).from(Tables.PLAYERS).where(Tables.PLAYERS.UUID.eq(player.getUniqueId().toString())))) {
            // They do

            // Get them
            PlayersRecord record = db.jooq().selectFrom(Tables.PLAYERS).where(Tables.PLAYERS.UUID.eq(player.getUniqueId().toString())).fetchOne();

            // Update
            record.setLastUsername(player.getName());
            record.setLastCkey(ckey);
            record.setLastSeen(db.now());

            // Save
            record.store();

        } else {
            // They dont

            // Make a record
            PlayersRecord record = db.jooq().newRecord(Tables.PLAYERS);

            // Set everything
            record.setUuid(player.getUniqueId().toString());
            record.setLastUsername(player.getName());
            record.setLastCkey(ckey);
            record.setFirstSeen(db.now());
            record.setLastSeen(db.now());

            // Save
            record.store();
        }
    }

    // Set just their lastseen on disconnect
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Get them
        PlayersRecord record = db.jooq().selectFrom(Tables.PLAYERS).where(Tables.PLAYERS.UUID.eq(event.getPlayer().getUniqueId().toString())).fetchOne();

        // Update
        record.setLastSeen(db.now());

        // Save
        record.store();
    }
}
