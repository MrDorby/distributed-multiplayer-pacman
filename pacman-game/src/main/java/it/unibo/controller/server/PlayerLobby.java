package it.unibo.controller.server;

import java.util.List;

/**
 * Represents a lobby responsible for managing a collection of players waiting for a game to start.
 */
public interface PlayerLobby {
    /**
     * Attempts to add a player to the lobby.
     *
     * @param playerName the unique identifier of the player joining
     */
    void addPlayer(String playerName);

    /**
     * Checks whether the lobby has been moved into the active playing state.
     *
     * @return true if the lobby is currently playing, false otherwise
     */
    boolean isPlaying();

    /**
     * Retrieves the number of players currently sitting in the lobby.
     *
     * @return the current player count
     */
    int getCurrentPlayerCount();

    /**
     * Retrieves the total number of players required for this lobby.
     *
     * @return the target player capacity
     */
    int getRequiredPlayerCount();

    /**
     * Returns whether the lobby is at full capacity.
     * @return a boolean
     */
    boolean isFull();

    /**
     * Returns the players currently in the lobby.
     *
     * @return a {@code List} containing the names of all joined players
     */
    List<String> getPlayers();
}
