package it.unibo.controller.client;

import it.unibo.controller.client.network.sockets.session.ConnectionState;
import it.unibo.controller.shared.network.dto.GameContextDTO;

public interface GameClientListener {
    void onGameStarted();
    void onConnectionStateChanged(ConnectionState state);
    void onGameEnded(GameContextDTO gameContext);
}