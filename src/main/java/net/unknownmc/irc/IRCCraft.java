package net.unknownmc.irc;

import org.bukkit.plugin.java.JavaPlugin;

public class IRCCraft extends JavaPlugin {
    public IRCBot bot;

    public void onEnable() {
        this.bot = new IRCBot(this, getConfig().getString("irc.hostname"), getConfig().getInt("irc.port"), getConfig().getString("irc.password"), getConfig().getString("irc.channel"));
        this.bot.runTaskAsynchronously(this);
    }

    public void onDisable() {

    }
}
