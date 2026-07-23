package it.unibo.controller.server.lobby;

import it.unibo.controller.server.network.sockets.session.GameSessionLifecycleListener;

import java.util.Collection;

/**
 * Manages match flow, lobby transitions, and player session lifecycle events.
 */
public interface MatchLifecycleManager extends GameSessionLifecycleListener {

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
     * Returns the usernames of the people playing the game.
     *
     * @return a {@link Collection} of usernames
     */
    Collection<String> getActivePlayers();

    /**
     * Triggered when the underlying game server completes its startup sequence.
     * Implementations can use this hook to initialize pre-game timers or grace periods.
     */
    void onServerStart();
}
