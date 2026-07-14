package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class GameSessionController {
    private static final Logger logger = LoggerFactory.getLogger(GameSessionController.class);

    private final GameSessionRegistry registry = new GameSessionRegistry();
    private final List<SessionLifecycleListener> listeners = new ArrayList<>();

    public void addListener(SessionLifecycleListener listener) {
        this.listeners.add(listener);
    }

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
        session.setState(SessionState.CONNECTING);
        registry.add(session);
        return session;
    }

    private GameSession reconnect(GameSession session, Channel newChannel) {
        Channel oldChannel = session.getTcpChannel();
        session.setTcpChannel(newChannel);
        session.setUdpAddress(null);
        session.setState(SessionState.RECONNECTING);
        if (oldChannel != null && oldChannel != newChannel && oldChannel.isOpen()) {
            oldChannel.close();
        }
        return session;
    }

    private void issueUdpToken(GameSession session) {
        String token = UUID.randomUUID().toString();
        session.setUdpToken(token);
    }

    public void onUdpHandshake(String token, InetSocketAddress sender) {
        GameSession session = registry.getByUdpToken(token);
        if (session == null) {
            logger.warn("Rejected UDP handshake packet from {}. Token was invalid.", sender);
            return;
        }
        SessionState previousState = session.getState();
        session.setUdpAddress(sender);
        session.setUdpToken(null);
        session.setState(SessionState.CONNECTED);
        logger.info("Handshake with player {} succeeded. UDP bound to: {}", session.getUsername(), sender);
        for (SessionLifecycleListener listener : listeners) {
            if (previousState == SessionState.RECONNECTING) {
                listener.onPlayerReconnected(session);
            } else {
                listener.onPlayerConnected(session);
            }
        }
    }

    public void onDisconnect(GameSession session) {
        if (session == null) return;
        logger.info("Connection lost for player {} {}", session.getUsername(), session.getUdpAddress());
        session.setState(SessionState.DISCONNECTED);
        Channel tcpChannel = session.getTcpChannel();
        if (tcpChannel != null && tcpChannel.isOpen()) {
            tcpChannel.close();
        }
        for (SessionLifecycleListener listener : listeners) {
            listener.onPlayerDisconnected(session);
        }
    }

    public GameSession getSessionByChannel(Channel channel) {
        return registry.getByChannel(channel);
    }

    public GameSession getSessionByUsername(String username) {
        return registry.getByUsername(username);
    }

    public Collection<GameSession> getAllSessions() {
        return registry.getSessions();
    }
}
