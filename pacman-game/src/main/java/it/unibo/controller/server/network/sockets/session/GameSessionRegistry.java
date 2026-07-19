package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe registry of player sessions.
 *
 * <p>Sessions are indexed by username, which is the unique identifier for a
 * player session. The registry provides lookups by username, TCP channel,
 * UDP endpoint, and UDP handshake token.</p>
 */
public class GameSessionRegistry {
    private final ConcurrentMap<String, GameSession> sessionsByUsername = new ConcurrentHashMap<>();

    public void add(GameSession session) {
        sessionsByUsername.put(session.getUsername(), session);
    }

    public void remove(GameSession session) {
        sessionsByUsername.remove(session.getUsername());
    }

    public GameSession getByUsername(String username) {
        return sessionsByUsername.get(username);
    }

    public GameSession getByChannel(Channel channel) {
        if (channel == null) return null;
        return sessionsByUsername.values().stream()
                .filter(session -> channel.equals(session.getTcpChannel()))
                .findFirst()
                .orElse(null);
    }

    public GameSession getByUdpAddress(InetSocketAddress address) {
        if (address == null) return null;
        return sessionsByUsername.values().stream()
                .filter(session -> address.equals(session.getUdpAddress()))
                .findFirst()
                .orElse(null);
    }

    public GameSession getByUdpToken(String token) {
        if (token == null) return null;
        return sessionsByUsername.values().stream()
                .filter(session -> session.getUdpToken() != null && token.equals(session.getUdpToken().value()))
                .findFirst()
                .orElse(null);
    }

    public Collection<GameSession> getSessions() {
        return sessionsByUsername.values();
    }
}