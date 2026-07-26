package it.unibo.dto;

/**
 * 
 * Defines the type of the response for the lobby request.
 */
public enum LobbyTypeResponse {
    /**
     * If the lobby has been found.
     */
    FOUND,

    /**
     * If the lobby has not been found yet.
     */
    WAITING
}
