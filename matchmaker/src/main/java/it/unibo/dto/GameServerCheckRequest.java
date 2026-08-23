package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to check the availability of the GameServer to the Manager.
 * @param matchID the identifier of the match.
 * @param serverName the unique name of the GameServer.
 * @param timeLeft the time left from the end of the match.
 */
public record GameServerCheckRequest(
    @JsonProperty("match-id") String matchID,
    @JsonProperty("server-name") String serverName,
    @JsonProperty("time-left") long timeLeft
) {
    
}
