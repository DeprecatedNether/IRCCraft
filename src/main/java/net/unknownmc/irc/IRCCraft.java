package net.unknownmc.irc;

import net.unknownmc.irc.apilisteners.AntiAdvertiser;
import net.unknownmc.irc.apilisteners.UnknownChat;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class IRCCraft extends JavaPlugin {
    private IRCBot bot;

    public void onEnable() {
        saveDefaultConfig();
        this.bot = new IRCBot(this, getConfig().getString("irc.hostname"), getConfig().getInt("irc.port"), getConfig().getString("irc.password"), getConfig().getString("irc.channel"));
        this.bot.runTaskAsynchronously(this);
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        Plugin antiadvertiser = getServer().getPluginManager().getPlugin("AntiAdvertiser");
        if (antiadvertiser != null && antiadvertiser.isEnabled() && getConfig().getBoolean("actions.antiadvertiser")) {
            getServer().getPluginManager().registerEvents(new AntiAdvertiser(this), this);
        }
        Plugin unknownchat = getServer().getPluginManager().getPlugin("UnknownChat");
        if (unknownchat!= null && unknownchat.isEnabled() && getConfig().getBoolean("actions.unknownchat")) {
            getServer().getPluginManager().registerEvents(new UnknownChat(this), this);
        }
    }

    public void onDisable() {
        bot.disconnect();
        bot.cancel(); // Just in case the above failed
    }

    /**
     * Gets the instance of IRCBot.
     * @return
     */
    public IRCBot getIRCBot() {
        return bot;
    }
}
