package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for the GameServerManager to instantiate a new GameServer
 * for the specified match.
 * @param matchID the identifier of the new match.
 * @param mapID the name of the map chosen for this match.
 */
public record ManagerCreateServer(
    @JsonProperty("match-id") String matchID,
    @JsonProperty("map-id") String mapID) {

}
