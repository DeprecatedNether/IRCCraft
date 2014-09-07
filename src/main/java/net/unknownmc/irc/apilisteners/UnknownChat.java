/*
 * AntiAdvertiser
 * Copyright (C) 2014  DeprecatedNether
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
