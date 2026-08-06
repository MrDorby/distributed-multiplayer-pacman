package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response received by the GameServerManager to check the availability of the GameServer.
 * @param status defines the status of the GameServer.
 * @param serverInfo the information about the GameServer in case the State is UNHEALTHY,
 * otherwise is null.
 */
public record GameServerCheckResponse(
    @JsonProperty("status") GameServerStatus status,
    @JsonProperty("server-info") GameServerInfo serverInfo) {

}
