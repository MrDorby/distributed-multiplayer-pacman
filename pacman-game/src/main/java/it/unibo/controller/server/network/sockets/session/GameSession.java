package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Represents an active player session in the game server, managing state across both TCP and UDP transport layers.
 */
public class GameSession {
    private final String username;
    private Channel tcpChannel;
    private InetSocketAddress udpAddress;
    private String udpToken;

    private volatile SessionState state = SessionState.CONNECTED;

    /**
     * Constructs a new session initialized via a TCP connection.
     * @param username      the unique username of the connected player
     * @param tcpChannel    the active channel associated with the player's TCP connection
     */
    public GameSession(String username, Channel tcpChannel) {
        this.username = username;
        this.tcpChannel = tcpChannel;
    }

    /**
     * Returns the unique username identifying this session's player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the active TCP channel.
     */
    public synchronized Channel getTcpChannel() {
        return tcpChannel;
    }

    /**
     * Binds or updates the primary TCP channel for this session.
     */
    public synchronized void setTcpChannel(Channel tcpChannel) {
        this.tcpChannel = tcpChannel;
    }

    /**
     * Returns the remote UDP address of the client.
     */
    public synchronized InetSocketAddress getUdpAddress() {
        return udpAddress;
    }

    /**
     * Binds the client's discovered remote UDP address.
     */
    public synchronized void setUdpAddress(InetSocketAddress udpAddress) {
        this.udpAddress = udpAddress;
    }

    /**
     * Returns the secret handshake token expected from the client over UDP.
     */
    public synchronized String getUdpToken() {
        return udpToken;
    }

    /**
     * Sets the secret handshake token. This token should be unique and sent
     * to the client over TCP to perform the UDP endpoint discovery.
     */
    public synchronized void setUdpToken(String udpToken) {
        this.udpToken = udpToken;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }
}
