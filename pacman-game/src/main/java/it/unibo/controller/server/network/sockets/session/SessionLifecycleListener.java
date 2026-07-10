package it.unibo.controller.server.network.sockets.session;

public interface SessionLifecycleListener {
    void onPlayerConnected(GameSession session);
    void onPlayerReconnected(GameSession session);
    void onPlayerDisconnect(GameSession session);
}
