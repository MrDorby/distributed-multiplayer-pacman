package it.unibo.controller.server.network.transport;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameSessionRegistry {
    private final ConcurrentMap<String, GameUserSession> sessions = new ConcurrentHashMap<>();

    public GameUserSession register(String username, Channel tcpChannel) {
        GameUserSession session = new GameUserSession(username, tcpChannel);
        sessions.put(username, session);
        tcpChannel.closeFuture().addListener(_ -> sessions.remove(username, session));
        return session;
    }

    public GameUserSession get(String username) {
        return sessions.get(username);
    }

    public Collection<GameUserSession> all() {
        return sessions.values();
    }
}
