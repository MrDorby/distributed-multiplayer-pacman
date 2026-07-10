package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSessionController {
    private static final Logger logger = LoggerFactory.getLogger(GameSessionController.class);

    private final GameSessionRegistry registry;
    private final List<SessionLifecycleListener> listeners = new ArrayList<>();

    public GameSessionController(GameSessionRegistry registry) {
        this.registry = registry;
    }

    public void addListener(SessionLifecycleListener listener) {
        this.listeners.add(listener);
    }

    public GameSession onTcpConnect(String username, Channel channel) {
        GameSession currentSession = registry.getByUsername(username);
        GameSession session;
        if (currentSession != null) {
            logger.info("Reconnecting {} to existing session", username);
            session = reconnect(currentSession, channel);
        } else {
            logger.info("Creating new session for {}", username);
            session = connect(username, channel);
        }
        issueUdpToken(session);
        return session;
    }

    private GameSession connect(String username, Channel channel) {
        GameSession session = new GameSession(username, channel);
        registry.add(session);
        return session;
    }

    private GameSession reconnect(GameSession session, Channel newChannel) {
        Channel oldChannel = session.getTcpChannel();
        session.setTcpChannel(newChannel);
        session.setUdpAddress(null);
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
        session.setUdpAddress(sender);
        session.setUdpToken(null);
        logger.info("Handshaking with player {} succeeded. UDP bound to remote endpoint: {}", session.getUsername(), sender);
        for (SessionLifecycleListener listener : listeners) {
            listener.onPlayerConnected(session);
        }
    }
}
