package it.unibo.controller.server.network;

import it.unibo.model.game.GameContext;

public interface GameBroadcaster {
    void broadcast(GameContext context);
}