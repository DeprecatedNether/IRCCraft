package net.unknownmc.irc.apilisteners;

import net.unknownmc.antiadvertiser.api.PlayerAdvertiseEvent;
import net.unknownmc.irc.IRCBot;
import net.unknownmc.irc.IRCCraft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AntiAdvertiser implements Listener {

    IRCCraft main;
    IRCBot bot;

    public AntiAdvertiser(IRCCraft main) {
        this.main = main;
        this.bot = this.main.getIRCBot();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void advertise(PlayerAdvertiseEvent e) {
        bot.sendMessageToIRC("[AntiAdvertiser] " + e.getPlayer().getName() + " advertised using " + e.getType().toString().toLowerCase() + ": " + e.getMessage());
    }
}
