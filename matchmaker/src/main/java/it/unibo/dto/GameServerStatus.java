package it.unibo.dto;

/**
 * The status of a GameServer received during the check.
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