package me.aa07.parautil.spigot.discord;

import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.ConfigurationManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

// This class is the discord bot itself
public class DiscordBot implements MessageCreateListener {
    private DiscordApi api;
    private DiscordManager discordManager;
    private ConfigurationManager config;
    private TextChannel channel;

    public DiscordBot(ParaUtilSpigot plugin, DiscordManager discordManager, ConfigurationManager config) {
        this.discordManager = discordManager;
        this.config = config;

        api = new DiscordApiBuilder().setToken(config.discordConfiguration.token).login().join();
        plugin.getLogger().info(String.format("[DiscordManager] Logged in as %s / %s", api.getYourself().getName(), api.getYourself().getId()));

        channel = api.getTextChannelById(config.discordConfiguration.channel).get();
        plugin.getLogger().info(String.format("[DiscordManager] Set channel to #%s", channel.getId()));

        api.addMessageCreateListener(this);
        plugin.getLogger().info("[DiscordManager] Setup channel listener");
    }

    public void sendChatMessage(String author, String message) {
        if (message.contains("@")) {
            return; // Dont even try
        }

        channel.sendMessage(String.format("**[%s]** 💬 `%s` %s", config.generalConfiguration.serverId, author, message));
    }

    public void sendMessage(String message) {
        channel.sendMessage(String.format("**[%s]** %s", config.generalConfiguration.serverId, message));
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        // Only our channel
        if (event.getChannel() != channel) {
            return;
        }

        // No self talking
        if (event.getMessage().getAuthor().getId() == api.getYourself().getId()) {
            return;
        }

        discordManager.broadcastMessage(event.getMessageAuthor().getId(), event.getMessageContent());
    }
}
