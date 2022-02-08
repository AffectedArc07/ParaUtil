package me.aa07.parautil.spigot.configuration.discord;

import me.aa07.parautil.spigot.ParaUtilSpigot;
import me.aa07.parautil.spigot.configuration.sections.DiscordConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

// This class is the discord bot itself
public class DiscordBot implements MessageCreateListener {
    private DiscordApi api;
    private DiscordManager discordManager;
    private TextChannel channel;

    public DiscordBot(ParaUtilSpigot plugin, DiscordManager discordManager, DiscordConfiguration config) {
        this.discordManager = discordManager;

        api = new DiscordApiBuilder().setToken(config.token).login().join();
        plugin.getLogger().info(String.format("[DiscordManager] Logged in as %s / %s", api.getYourself().getName(), api.getYourself().getId()));

        channel = api.getTextChannelById(config.channel).get();
        plugin.getLogger().info(String.format("[DiscordManager] Set channel to #%s", channel.getId()));

        api.addMessageCreateListener(this);
        plugin.getLogger().info("[DiscordManager] Setup channel listener");
    }

    public void sendChatMessage(String author, String message) {
        if (message.contains("@everyone") || message.contains("@here")) {
            return; // Dont even try
        }

        channel.sendMessage(String.format("**[MC]** `<%s>` %s", author, message));
    }

    public void sendRaw(String message) {
        channel.sendMessage(message);
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
