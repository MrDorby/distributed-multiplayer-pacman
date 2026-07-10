package it.unibo.controller.server.network.sockets.session;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameSessionRegistry {
    private final ConcurrentMap<String, GameSession> sessions = new ConcurrentHashMap<>();

    public void add(GameSession session) {
        sessions.put(session.getUsername(), session);
    }

    public void remove(String username) {
        sessions.remove(username);
    }

    public GameSession getByUsername(String username) {
        return sessions.get(username);
    }

    public GameSession getByUdpToken(String token) {
        if (token == null) {
            return null;
        }
        return sessions.values().stream()
                .filter(session -> token.equals(session.getUdpToken()))
                .findFirst()
                .orElse(null);
    }

    public Collection<GameSession> getSessions() {
        return sessions.values();
    }
}