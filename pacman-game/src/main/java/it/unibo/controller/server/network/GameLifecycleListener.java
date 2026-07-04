package it.unibo.controller.server.network;

import it.unibo.model.game.GameContext;

public interface GameLifecycleListener {
    void onGameEnded(GameContext finalContext);
}
