package it.unibo.controller.server.lobby;

import it.unibo.controller.server.network.sockets.session.GameSessionLifecycleListener;

import java.util.Set;

/**
 * Manages match flow, lobby transitions, and player session lifecycle events.
 */
public interface LobbyManager extends GameSessionLifecycleListener {

    /**
     * Gets the current state of the match lobby.
     *
     * @return the current {@link LobbyState}
     */
    LobbyState getState();

    /**
     * Sets the state of the match lobby.
     *
     * @param state the new {@link LobbyState}
     */
    void setState(LobbyState state);

    /**
     * Returns a set of currently connected players.
     *
     * @return a {@link Set} containing usernames of connected players
     */
    Set<String> getConnectedPlayers();

    /**
     * Triggered when the underlying game server completes its startup sequence.
     * Implementations can use this hook to initialize pre-game timers or grace periods.
     */
    void onServerStart();
}
