package it.unibo.controller.server.lobby;

import java.util.List;

/**
 * Represents a lobby responsible for managing a collection of players waiting for a game to start.
 */
public interface PlayerLobby {
    /**
     * Adds a player to the lobby.
     *
     * @param playerName the unique identifier of the player joining
     */
    void addPlayer(String playerName);

    /**
     * Removes a player from the lobby.
     *
     * @param playerName the unique identifier of the player to be removed
     */
    void removePlayer(String playerName);

    /**
     * Returns the state of the lobby.
     *
     * @return a {@link LobbyState}
     */
    LobbyState getState();

    /**
     * Sets the state of the lobby.
     *
     * @param state the state to set the lobby to
     */
    void setState(LobbyState state);

    /**
     * Retrieves the number of players currently in the lobby.
     *
     * @return the current player count
     */
    int getCurrentPlayerCount();

    /**
     * Retrieves the total number of players required for the lobby to start playing.
     *
     * @return the max player capacity
     */
    int getRequiredPlayerCount();

    /**
     * Returns whether the lobby is at full capacity.
     *
     * @return a boolean
     */
    boolean isFull();

    /**
     * Returns the identifiers of the players currently in the lobby.
     *
     * @return a {@code List} containing the names of all joined players
     */
    List<String> getPlayers();
}
