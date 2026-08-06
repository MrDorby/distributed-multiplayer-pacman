package it.unibo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to check the availability of the GameServer to the Manager.
 * @param name the unique name of the GameServer.
 * @param timeLeft the time left from the end of the match.
 */
public record GameServerCheckRequest(
    @JsonProperty("serverName") String name,
    @JsonProperty("timeLeft") long timeLeft
) {
    
}
