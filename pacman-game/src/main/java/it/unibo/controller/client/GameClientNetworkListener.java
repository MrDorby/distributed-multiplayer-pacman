package it.unibo.controller.client;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.model.game.GameContext;

/**
 * Listener interface for handling incoming authoritative lifecycle events, signals and game contexts
 * dispatched from the remote game server.
 */
public interface GameClientNetworkListener {

    /**
     * Callback triggered when an authoritative snapshot is received from the server.
     *
     * @param context the authoritative snapshot of the game world state at a specific server tick
     */
    void onGameContext(GameContext context);

    /**
     * Callback triggered by the server to signal the match start.
     */
    void onGameStart();

    /**
     * Callback triggered when the match concludes according to the server.
     */
    void onGameEnd(GameContextDTO gameContextDTO);
}