package it.unibo.controller.network;

import it.unibo.model.game.GameContext;

public interface NetworkServer {
    void broadcast(GameContext context);
}
