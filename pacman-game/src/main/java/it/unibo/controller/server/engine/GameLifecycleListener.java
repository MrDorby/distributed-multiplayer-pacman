package it.unibo.controller.server.engine;

import it.unibo.model.game.GameContext;

/**
 * Notified when a game reaches its end.
 */
public interface GameLifecycleListener {
    /**
     * Called once the game has ended, with its final context.
     *
     * @param finalContext the game's context at the moment the game ended
     */
    void onGameEnded(GameContext finalContext);
}
