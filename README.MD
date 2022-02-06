# Paradise Plugins

Plugins designed to be deployed on Minecraft servers deployed inside the Paradise SS13 environment.

These plugins provide account linkage functionality to:

- Soft-whitelist a server to verified users only
- Automatically deny users who are banned ingame
- Translate MC usernames to their linked ckeys
- Automatically grant permissions to ingame people based on forum groups

## Provided Projects

- `ParaUtilDatabase` - Database project automatically generated with JOOQ (Use `mvn package -P update-db` to update)
- `ParaUtilBungee` - BungeeCord plugin to support sending player pings to a Minecraft server via plugin channels.
  - This is done since Bungee can do a much better job at retrieving a user's ping than the stock server can
- `ParaUtilSpigot` - Spigot plugin to provide the features listed above. It is compatible with:
  - Spigot 1.12.2 (Running under Mohist, a forge+spigot compat server)

## Building

You *will* need to install the Spigot build locally in order to compile these plugins. This can be done with [BuildTools](https://www.spigotmc.org/wiki/buildtools/) by running

```bash
java -jar BuildTools.jar --rev 1.12.2
```

**NOTE:** This requires an install of Java 8. You may have to manually specify `JAVA_HOME` if your system is not setup to do automatic detections.

After this, you should be able to run `mvn package` to generate the JAR files. These will appear in the `out` directory.

## Testing

You will not be able to test the login checking & ckey translation features due to them requiring the Paradise backend architecture which is not publicly accessible (for obvious reasons). You will also need the server sat behind BungeeCord to facilitate testing the ping measuring feature. You can disable this via setting `devmode` to `true` in the plugin configuration.

## Code Guidelines

- Code must be tested
- Code must adhere to the style guidelines set in `checkstyle.xml`
  - These are google's guidelines but with minor tweaks for my own needs
