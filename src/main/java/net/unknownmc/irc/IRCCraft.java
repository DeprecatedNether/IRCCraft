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
            getLogger().info("Enabling AntiAdvertiser listener");
            getServer().getPluginManager().registerEvents(new AntiAdvertiser(this), this);
        } else {
            getLogger().info("AntiAdvertiser listener is disabled");
        }
        Plugin unknownchat = getServer().getPluginManager().getPlugin("UnknownChat");
        if (unknownchat!= null && unknownchat.isEnabled() && getConfig().getBoolean("actions.unknownchat")) {
            getLogger().info("Enabling UnknownChat listener");
            getServer().getPluginManager().registerEvents(new UnknownChat(this), this);
        } else {
            getLogger().info("UnknownChat listener is disabled");
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

    public void sendDebug(String message) {
        if (getConfig().getBoolean("debug")) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}
