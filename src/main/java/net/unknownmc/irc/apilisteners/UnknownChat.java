package net.unknownmc.irc.apilisteners;

import net.unknownmc.irc.IRCBot;
import net.unknownmc.irc.IRCCraft;
import net.unknownmc.mc.chat.WhisperEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UnknownChat implements Listener {

    IRCCraft main;
    IRCBot bot;

    public UnknownChat(IRCCraft main) {
        this.main = main;
        this.bot = main.getIRCBot();
    }

    @EventHandler
    public void whisper(WhisperEvent e) {
        bot.sendMessageToIRC("[TELL] " + e.getFrom().getName() + " -> " + e.getTo().getName() + ": " + e.getMessage());
    }
}
