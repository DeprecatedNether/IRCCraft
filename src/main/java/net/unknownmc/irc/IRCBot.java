package net.unknownmc.irc;

import org.bukkit.entity.Player;
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
                } else {
                    processIncomingMessage(ln);
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

    private void processIncomingMessage(String raw) {
        if (!raw.startsWith(":")) return;
        final String[] info = raw.substring(1).split(" :"); // [0] ->  message details; [1] -> message
        String[] split1 = info[0].split(" "); // [0] -> user info; [1] -> action (privmsg); [2] -> channel name
        if (split1.length != 3 || !split1[1].equalsIgnoreCase("privmsg")) return;
        String[] split2 = info[0].split("!"); // [0] -> nickname; [1] -> ~ident@hostname
        String[] split3 = split1[1].substring(1).split("@"); // [0] -> ident; [1] -> hostname

        String nick = split2[0];
        String ident = split3[0];
        String hostname = split3[1];
        String channel = split1[2];
        String message = info[1];

        if (!channel.equals(this.channel)) return;
        if (!main.getConfig().contains("irc-users." + nick)) return; // User not authorized, ignore message
        String requiredIdent = main.getConfig().getString("irc-users." + nick + ".ident");
        if (requiredIdent != null && !requiredIdent.equals(ident)) return; // ident is required by the config, but the sender's ident doesn't match
        String requiredHostname = main.getConfig().getString("irc-users." + nick + ".hostname");
        if (requiredHostname != null && !requiredHostname.equals(hostname)) return; // hostname is required by the config, but the sender's hostname doesn't match
        int permission = main.getConfig().getInt("irc-users." + nick + ".permission-level");

        if (message.startsWith("?")) { // passive
            if (permission < 0) { // why'd you set it to -1 though?
                sendMessageToIRC(nick + ": You are not authorized to use this command.");
                return;
            }
            String[] args = message.substring(1).split(" ");
            if (args[0].equalsIgnoreCase("list")) {
                String players = "";
                for (Player player : main.getServer().getOnlinePlayers()) {
                    if (players.length() != 0) {
                        players = players + ", ";
                    }
                    players = players + player.getName();
                }
                sendMessageToIRC(nick + ": Currently online: " + players + ".");
                return;
            }
            sendMessageToIRC(nick + ": Unknown command.");
         }

        if (message.startsWith("!")) { // command from console
            if (permission < 2) {
                sendMessageToIRC(nick + ": You are not authorized to use this command.");
                return;
            }
            final String command = message.substring(1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    main.getLogger().info(info[0] + " issued console command: " + command); // nick!~ident@hostname
                    main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command);
                }
            }.runTask(main);
            sendMessageToIRC(nick + ": Issued command '" + command + "'");
            return;
        }
    }
}
