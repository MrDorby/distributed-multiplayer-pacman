package it.unibo.controller.client.network;

import it.unibo.model.game.GameContext;

public interface GameClientNetworkListener {
    void onGameContext(GameContext context);

    void onGameStart();
}