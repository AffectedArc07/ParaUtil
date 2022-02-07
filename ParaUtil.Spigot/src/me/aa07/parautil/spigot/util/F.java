package me.aa07.parautil.spigot.util;

import org.bukkit.ChatColor;

// Easy method for easy string formatting for consistency
public class F {
    public static String pri(String source, String message) {
        return ChatColor.GRAY + "[" + ChatColor.GOLD + source + ChatColor.GRAY + "] " + ChatColor.YELLOW + message;
    }

    public static String pri(String message) {
        return pri("ParaUtil", message);
    }
}
