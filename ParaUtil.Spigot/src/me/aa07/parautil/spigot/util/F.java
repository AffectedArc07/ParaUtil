package me.aa07.parautil.spigot.util;

import org.bukkit.ChatColor;

// Easy method for easy string formatting for consistency
public class F {
    private static ChatColor textPri = ChatColor.YELLOW;
    private static ChatColor textSec = ChatColor.GOLD;

    public static String pri(String source, String message) {
        return ChatColor.GRAY + "[" + textSec + source + ChatColor.GRAY + "] " + textPri + message;
    }

    public static String pri(String message) {
        return pri("ParaUtil", message);
    }

    // Easy text wrapper
    public static String item(String entry) {
        return textSec + entry + textPri;
    }
}
