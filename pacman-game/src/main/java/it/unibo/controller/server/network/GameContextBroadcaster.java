package it.unibo.controller.server.network;

import it.unibo.model.game.GameContext;

/**
 * Receives game state updates from the engine to be broadcast to connected clients.
 */
public interface GameContextBroadcaster {
    /**
     * Called by the engine with the current game context.
     *
     * @param context the current game context
     */
    void broadcast(GameContext context);
}