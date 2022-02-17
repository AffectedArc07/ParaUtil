package me.aa07.parautil.spigot.configuration.sections;

import java.util.HashMap;

public class GeneralConfiguration {
    // Are we in dev mode
    public boolean devmode;
    // UUID:CKey map of people who should bypass login rules
    public HashMap<String, String> userMap = new HashMap<String, String>();
}
