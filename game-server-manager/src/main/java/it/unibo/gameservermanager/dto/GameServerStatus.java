package it.unibo.gameservermanager.dto;

/**
 * The current status of a GameServer.
 */
public enum GameServerStatus {
    /**
     * The GameServer is functioning properly.
     */
    HEALTHY,
    /**
     * The GameServer stopped sending health checks, and is not working properly.
     */
    UNHEALTHY,
    /**
     * The specified GameServer was not found.
     */
    NOT_FOUND
}
