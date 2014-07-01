package net.unknownmc.irc;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Listeners implements Listener {

    IRCCraft main;
    IRCBot bot;

    public Listeners(IRCCraft main) {
        this.main = main;
        this.bot = this.main.getIRCBot();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (!main.getConfig().getBoolean("actions.join")) return;
        bot.sendMessageToIRC("[JOIN] " + e.getPlayer().getName());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (!main.getConfig().getBoolean("actions.leave")) return;
        bot.sendMessageToIRC("[QUIT] " + e.getPlayer().getName());
    }

    @EventHandler
    public void kick(PlayerKickEvent e) {
        if (!main.getConfig().getBoolean("actions.kick")) return;
        bot.sendMessageToIRC("[KICK] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getReason().replace("\n", "")));
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (!main.getConfig().getBoolean("actions.chat") || e.isCancelled()) return;
        bot.sendMessageToIRC("[CHAT] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getMessage()));
    }

    @EventHandler (ignoreCancelled = true)
    public void muted(AsyncPlayerChatEvent e) {
        if (!main.getConfig().getBoolean("actions.muted") || e.getFormat().equalsIgnoreCase("abc") || !e.isCancelled()) return; // The abc part is for one of my private plugins. Remove in forks
        bot.sendMessageToIRC("[MUTED] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getMessage()));
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().split(" ")[0].substring(1);
        if (main.getConfig().isString("commands." + cmd)) {
            bot.sendMessageToIRC(main.getConfig().getString("commands." + cmd).replace("{cmd}", e.getMessage()).replace("{player}", e.getPlayer().getName()));
        }
    }
}
