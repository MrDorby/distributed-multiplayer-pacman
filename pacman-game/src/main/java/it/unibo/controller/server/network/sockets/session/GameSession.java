package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Represents a player's server-side session and tracks their connection state across TCP and UDP transports.
 *
 * <p>A session is created when a player establishes a TCP connection. The TCP
 * connection is used as the reliable communication channel and to perform the
 * initial UDP handshake.</p>
 *
 * <p>The session then progresses through the following lifecycle:</p>
 *
 * <ul>
 *     <li>{@link GameSessionState#CONNECTING}: The TCP connection is established,
 *     but the client has not yet completed the UDP token handshake.</li>
 *     <li>{@link GameSessionState#CONNECTED}: The session is fully active and both
 *     TCP and UDP communication channels are available for gameplay.</li>
 *     <li>{@link GameSessionState#DISCONNECTED}: The player's TCP connection was
 *     lost, but the session data is retained in memory to allow reconnection.</li>
 *     <li>{@link GameSessionState#RECONNECTING}: A previously disconnected player
 *     has established a new TCP connection and is waiting to complete the
 *     UDP handshake again.</li>
 * </ul>
 */
public class GameSession {
    private final String username;
    private Channel tcpChannel;
    private InetSocketAddress udpAddress;
    private UdpHandshakeToken udpToken;
    private GameSessionState state;

    /**
     * Creates a new player session after a TCP connection is established.
     *
     * @param username the unique username identifying the player
     * @param tcpChannel the player's active TCP channel
     */
    public GameSession(String username, Channel tcpChannel) {
        this.username = username;
        this.tcpChannel = tcpChannel;
        this.state = GameSessionState.CONNECTING;
    }

    /**
     * Returns the unique username associated with this session.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the currently active TCP channel used for reliable communication.
     *
     * @return the active TCP channel, or {@code null} if no channel is assigned
     */
    public synchronized Channel getTcpChannel() {
        return tcpChannel;
    }

    /**
     * Replaces the TCP channel associated with this session.
     *
     * <p>This is used during reconnection when a player establishes a new TCP
     * connection.</p>
     *
     * @param tcpChannel the new TCP channel
     */
    public synchronized void setTcpChannel(Channel tcpChannel) {
        this.tcpChannel = tcpChannel;
    }

    /**
     * Returns the remote UDP endpoint bound to this session.
     *
     * <p>This remains {@code null} until the client completes the UDP handshake.</p>
     *
     * @return the client's UDP address, or {@code null} if UDP is not bound
     */
    public synchronized InetSocketAddress getUdpAddress() {
        return udpAddress;
    }

    /**
     * Stores the remote UDP endpoint discovered during the handshake process.
     *
     * @param udpAddress the client's UDP address
     */
    public synchronized void setUdpAddress(InetSocketAddress udpAddress) {
        this.udpAddress = udpAddress;
    }

    /**
     * Returns the temporary UDP handshake token assigned to this session.
     *
     * <p>The token is sent to the client over TCP and must be returned in the
     * first UDP packet to prove ownership of the session.</p>
     *
     * @return the current UDP handshake token, or {@code null} after successful binding
     */
    public synchronized UdpHandshakeToken getUdpToken() {
        return udpToken;
    }

    /**
     * Assigns the temporary UDP handshake token used to associate the first
     * UDP packet from the client with this session.
     *
     * <p>The token is normally generated after TCP connection establishment or
     * reconnection and is cleared once the UDP endpoint has been successfully
     * bound.</p>
     *
     * @param udpToken handshake token to associate with this session
     */
    public synchronized void setUdpToken(UdpHandshakeToken udpToken) {
        this.udpToken = udpToken;
    }

    /**
     * Attempts to complete the UDP handshake for this session.
     *
     * <p>The supplied token is compared against the currently assigned handshake
     * token. If the token matches and has not expired, the sender's UDP endpoint
     * is bound to this session, the handshake token is discarded, and the session
     * transitions to {@link GameSessionState#CONNECTED}.</p>
     *
     * @param token handshake token received from the client
     * @param sender client's UDP endpoint
     * @return the outcome of the handshake attempt
     */
    public synchronized UdpHandshakeResult completeUdpHandshake(String token, InetSocketAddress sender) {
        if (udpToken == null || !udpToken.value().equals(token)) {
            return UdpHandshakeResult.INVALID_TOKEN;
        }
        if (udpToken.expired()) {
            return UdpHandshakeResult.EXPIRED_TOKEN;
        }
        udpAddress = sender;
        udpToken = null;
        state = GameSessionState.CONNECTED;
        return UdpHandshakeResult.ACCEPTED;
    }

    /**
     * Returns the current lifecycle state of this session.
     *
     * @return the session state
     */
    public synchronized GameSessionState getState() {
        return state;
    }

    /**
     * Changes the lifecycle state of this session.
     *
     * @param state the new session state
     */
    public synchronized void setState(GameSessionState state) {
        this.state = state;
    }
}
