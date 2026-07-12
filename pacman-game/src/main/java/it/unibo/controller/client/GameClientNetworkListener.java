package it.unibo.controller.client;

import it.unibo.model.game.GameContext;

public interface GameClientNetworkListener {
    void onGameContext(GameContext context);

    void onGameStart();
}