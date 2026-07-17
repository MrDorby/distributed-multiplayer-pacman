package it.unibo.controller.client;

import it.unibo.controller.client.network.sockets.session.ConnectionState;

public interface GameClientListener {
    void onGameStarted();
    void onConnectionStateChanged(ConnectionState state);
}