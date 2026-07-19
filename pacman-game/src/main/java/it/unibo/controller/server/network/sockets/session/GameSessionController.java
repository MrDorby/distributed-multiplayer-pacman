package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Coordinates player session lifecycle events and maintains the collection of
 * active player sessions.
 *
 * <p>This controller manages the transition between TCP connection,
 * UDP endpoint binding, reconnection, and disconnection. A session is created
 * when a player connects over TCP, receives a temporary UDP handshake token,
 * and becomes fully active after the UDP handshake completes successfully.</p>
 */
public class GameSessionController {
    private static final Logger logger = LoggerFactory.getLogger(GameSessionController.class);
    private static final Duration UDP_HANDSHAKE_TIMEOUT = Duration.ofSeconds(15);

    private final GameSessionRegistry registry = new GameSessionRegistry();
    private final List<GameSessionLifecycleListener> listeners = new ArrayList<>();

    public void addListener(GameSessionLifecycleListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Handles a new TCP connection from a player.
     *
     * <p>If no existing session exists for the username, a new session is created
     * in the {@link GameSessionState#CONNECTING} state. If a previous session exists,
     * it is treated as a reconnection attempt and its TCP channel is replaced.</p>
     *
     * <p>A new UDP handshake token is issued after the TCP connection is accepted.
     * The client must complete the UDP handshake before the session becomes
     * {@link GameSessionState#CONNECTED}.</p>
     *
     * @param username unique identifier of the connecting player
     * @param channel newly established TCP channel
     * @return the created or reconnected player session
     */
    public GameSession onTcpConnect(String username, Channel channel) {
        GameSession currentSession = registry.getByUsername(username);
        GameSession session;
        if (currentSession != null) {
            logger.info("{} {} is reconnecting over TCP", username, channel.remoteAddress());
            session = reconnect(currentSession, channel);
        } else {
            logger.info("Creating new session for {} {}", username, channel.remoteAddress());
            session = connect(username, channel);
        }
        issueUdpToken(session);
        return session;
    }

    private GameSession connect(String username, Channel channel) {
        GameSession session = new GameSession(username, channel);
        session.setState(GameSessionState.CONNECTING);
        registry.add(session);
        return session;
    }

    private GameSession reconnect(GameSession session, Channel newChannel) {
        Channel oldChannel = session.getTcpChannel();
        session.setTcpChannel(newChannel);
        session.setUdpAddress(null);
        session.setState(GameSessionState.RECONNECTING);
        if (oldChannel != null && oldChannel != newChannel && oldChannel.isOpen()) {
            oldChannel.close();
        }
        return session;
    }

    private void issueUdpToken(GameSession session) {
        UdpHandshakeToken token = new UdpHandshakeToken(UUID.randomUUID().toString(), Instant.now().plus(UDP_HANDSHAKE_TIMEOUT));
        session.setUdpToken(token);
    }

    /**
     * Completes the UDP handshake for a player session.
     *
     * <p>The provided token is validated against pending sessions. If valid, the
     * client's UDP endpoint is bound, the handshake token is cleared, and the
     * session transitions to {@link GameSessionState#CONNECTED}.</p>
     *
     * @param token UDP handshake token provided by the client
     * @param sender remote UDP endpoint of the client
     * @return the result of the handshake validation
     */
    public UdpHandshakeResult onUdpHandshake(String token, InetSocketAddress sender) {
        GameSession session = registry.getByUdpToken(token);
        if (session == null) {
            return UdpHandshakeResult.INVALID_TOKEN;
        }
        return session.completeUdpHandshake(token, sender);
    }

    /**
     * Notifies lifecycle listeners that a session is ready for gameplay.
     *
     * <p>This method should be called only after the client has been informed that
     * UDP setup succeeded.</p>
     *
     * @param session session that completed transport setup
     * @param previousState state before the UDP handshake completed
     */
    public void onSessionReady(GameSession session, GameSessionState previousState) {
        for (GameSessionLifecycleListener listener : listeners) {
            if (previousState == GameSessionState.RECONNECTING) {
                listener.onPlayerReconnected(session);
            } else {
                listener.onPlayerConnected(session);
            }
        }
    }

    /**
     * Handles loss of a player's connection.
     *
     * <p>The session is transitioned to {@link GameSessionState#DISCONNECTED} and
     * retained in the registry so that the player may reconnect later. The active
     * TCP channel is closed if it is still open.</p>
     *
     * @param session session whose connection was lost
     */
    public void onDisconnect(GameSession session) {
        if (session == null) return;
        logger.info("Connection lost for player {} {}", session.getUsername(), session.getUdpAddress());
        session.setState(GameSessionState.DISCONNECTED);
        Channel tcpChannel = session.getTcpChannel();
        if (tcpChannel != null && tcpChannel.isOpen()) {
            tcpChannel.close();
        }
        for (GameSessionLifecycleListener listener : listeners) {
            listener.onPlayerDisconnected(session);
        }
    }

    public GameSession getSessionByChannel(Channel channel) {
        return registry.getByChannel(channel);
    }

    public GameSession getSessionByUdpToken(String token) {
        return registry.getByUdpToken(token);
    }

    public GameSession getSessionByUdpAddress(InetSocketAddress address) {
        return registry.getByUdpAddress(address);
    }

    public GameSession getSessionByUsername(String username) {
        return registry.getByUsername(username);
    }

    public Collection<GameSession> getAllSessions() {
        return registry.getSessions();
    }
}
