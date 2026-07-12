package it.unibo.controller.client;

import it.unibo.controller.shared.engine.GameEngine;

public interface GameClientController extends GameClientNetworkListener, GameCommandDispatcher {
    void connectToServer();

    GameEngine getEngine();

    void start();

    void stop();
}
