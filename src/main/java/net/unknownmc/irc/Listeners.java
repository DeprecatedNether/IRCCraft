package net.unknownmc.irc;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    IRCCraft main;

    public Listeners(IRCCraft main) {
        this.main = main;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (!main.getConfig().getBoolean("actions.join")) return;
        main.bot.sendMessageToIRC("[JOIN] " + e.getPlayer().getName());
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (!main.getConfig().getBoolean("actions.leave")) return;
        main.bot.sendMessageToIRC("[QUIT] " + e.getPlayer().getName());
    }

    @EventHandler
    public void kick(PlayerKickEvent e) {
        if (!main.getConfig().getBoolean("actions.kick")) return;
        main.bot.sendMessageToIRC("[KICK] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getReason().replace("\n", "")));
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (!main.getConfig().getBoolean("actions.chat") || e.isCancelled()) return;
        main.bot.sendMessageToIRC("[CHAT] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getMessage()));
    }

    @EventHandler (ignoreCancelled = true)
    public void muted(AsyncPlayerChatEvent e) {
        if (!main.getConfig().getBoolean("actions.muted") || e.getFormat().equalsIgnoreCase("abc") || !e.isCancelled()) return; // The abc part is for one of my private plugins. Remove in forks
        main.bot.sendMessageToIRC("[MUTED] " + e.getPlayer().getName() + ": " + ChatColor.stripColor(e.getMessage()));
    }
}
