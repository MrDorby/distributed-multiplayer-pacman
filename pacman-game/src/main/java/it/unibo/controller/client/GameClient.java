package it.unibo.controller.client;

import it.unibo.controller.shared.engine.GameEngine;

/**
 * This interface bridges the gap between the network transport layer and the local simulation loop.
 * It is responsible for orchestrating the client lifecycle, routing authoritative server updates into the engine,
 * and forwarding locally registered player commands back to the server.
 */
public interface GameClient extends GameClientNetworkListener, GameCommandListener {
    void joinServer();

    String getUsername();

    GameEngine getEngine();

    void addListener(GameClientListener listener);

    void start();

    void stop();
}
