package it.unibo.controller.server.lobby;

/**
 * Represents the lifecycle states of a multiplayer game lobby.
 */
public enum LobbyState {
    /**
     * The lobby is actively waiting for players to join before starting.
     */
    WAITING,
    /**
     * The game is actively in progress.
     */
    PLAYING,
    /**
     * The game has concluded.
     */
    FINISHED
}