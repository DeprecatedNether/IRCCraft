package net.unknownmc.irc;

import org.bukkit.plugin.java.JavaPlugin;

public class IRCCraft extends JavaPlugin {
    public void onEnable() {

    }

    public void onDisable() {

    }

    /**
     * Establishes a connection to an IRC server. This should be a ZNC bouncer (the plugin will make no attempts to set a nickname etc.)
     * @param hostname The hostname
     * @param port The port
     * @param pass The password to authenticate to ZNC with ("username:password")
     */
    public void connectToIRC(String hostname, int port, String pass) {

    }

    /**
     * Sends a message to the IRC channel.
     * @param message The message to send
     */
    public void sendMessageToIRC(String message) {

    }
}
