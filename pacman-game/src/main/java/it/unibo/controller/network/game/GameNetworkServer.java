package it.unibo.controller.network.game;

import it.unibo.model.game.GameContext;

public interface GameNetworkServer {
    void broadcast(GameContext context);
}
