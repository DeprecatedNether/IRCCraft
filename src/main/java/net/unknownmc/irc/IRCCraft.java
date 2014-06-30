package net.unknownmc.irc;

import org.bukkit.plugin.java.JavaPlugin;

public class IRCCraft extends JavaPlugin {
    public IRCBot bot;

    public void onEnable() {
        saveDefaultConfig();
        this.bot = new IRCBot(this, getConfig().getString("irc.hostname"), getConfig().getInt("irc.port"), getConfig().getString("irc.password"), getConfig().getString("irc.channel"));
        this.bot.runTaskAsynchronously(this);
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    public void onDisable() {
        bot.disconnect();
        bot.cancel(); // Just in case the above failed
    }
}
