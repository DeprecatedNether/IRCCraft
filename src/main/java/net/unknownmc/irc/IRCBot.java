package net.unknownmc.irc;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.Socket;

public class IRCBot extends BukkitRunnable {

    IRCCraft main;
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    String channel;
    boolean connected;

    /**
     * Establishes a connection to an IRC server.
     * @param hostname The hostname
     * @param port The port
     * @param pass The password to authenticate to ZNC with ("username:password")
     * @param channel The channel to join
     */
    public void connectToIRC(String hostname, int port, String pass, String channel) {
        try {
            this.socket = new Socket(hostname, port);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.writer.write("PASS " + pass + "\r\n");
            this.writer.write("USER irccraft +b * :IRCCraft\r\n");
            this.writer.write("NICK " + main.getConfig().getString("irc.nickname") + "\r\n");
            if (main.getConfig().getBoolean("irc.chanserv-invite")) {
                this.writer.write("PRIVMSG ChanServ :INVITE " + channel + "\r\n");
                this.writer.flush();
            }
            this.connected = true;
            // We have identified and may now start the loop in run()
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Sends a message to the IRC channel.
     * @param message The message to send
     */
    public void sendMessageToIRC(String message) {
        if (socket == null || !this.connected) {
            main.getLogger().severe("Discarding IRC message: " + message);
            return;
        }
        try {
            this.writer.write("PRIVMSG " + this.channel + " :" + message + "\r\n");
            this.writer.flush();
        } catch (IOException ioe) {
            main.getLogger().severe("Discarding IRC message: '" + message + "', reason: " + ioe.getMessage());
        }
    }

    public void disconnect() {
        this.connected = false;
    }

    @Override
    public void run() {
        if (this.socket == null || !this.connected) {
            return;
        }
        String ln;
        try {
            boolean joined = false;
            while ((ln = this.reader.readLine()) != null && this.connected) {
                if (ln.toLowerCase().startsWith("ping")) { // Respond to pings
                    this.writer.write("PONG " + ln.substring(5) + "\r\n");
                    this.writer.flush();
                } else if (!joined && ln.contains("376")) {
                    this.writer.write("JOIN " + channel + "\r\n");
                    this.writer.flush();
                    joined = true;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public IRCBot(IRCCraft plugin, String hostname, int port, String pass, String channel) {
        if (!channel.startsWith("#")) {
            plugin.getLogger().severe("That channel is invalid (" + channel + ")");
            return;
        }
        this.main = plugin;
        this.channel = channel;
        connectToIRC(hostname, port, pass, channel);
    }
}
