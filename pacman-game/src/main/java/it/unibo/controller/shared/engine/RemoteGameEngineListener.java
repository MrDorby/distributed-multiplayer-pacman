package it.unibo.controller.shared.engine;

import it.unibo.controller.shared.engine.event.GameEvent;
import it.unibo.controller.shared.network.dto.GameContextDTO;

/**
 * A network-agnostic listener interface designed to intercept lifecycle events
 * and simulation updates emitted directly by a game engine instance.
 */
public interface RemoteGameEngineListener {

    /**
     * Invoked when a macro-level simulation event occurs within the engine (e.g. game end)
     *
     * @param event the discrete internal engine event to be distributed or processed
     */
    void onGameEvent(GameEvent event);

    /**
     * Invoked at the end of a simulation tick when the engine has calculated a new complete snapshot of the game.
     *
     * <p>For an authoritative server engine this provides the snapshot that must be distributed across the
     * network to synchronize remote clients.
     *
     * @param context the complete snapshot of the game at the end of the current simulation tick
     */
    void onGameContextUpdate(GameContextDTO context);
}
