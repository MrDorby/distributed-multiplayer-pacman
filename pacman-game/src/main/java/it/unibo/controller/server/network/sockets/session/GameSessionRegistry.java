package it.unibo.controller.server.network.sockets.session;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public GameSession getByUdpToken(String token) {
        if (token == null) return null;
        return sessionsByUsername.values().stream()
                .filter(session -> token.equals(session.getUdpToken()))
                .findFirst()
                .orElse(null);
    }

    public Collection<GameSession> getSessions() {
        return sessionsByUsername.values();
    }
}